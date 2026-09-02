package revenera.gcs.domain

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.Configuration
import revenera.gcs.StringGenerator
import revenera.gcs.dmdemo.UserNotAuthenticatedFault
import revenera.gcs.dmdemo.UserNotFoundFault
import revenera.gcs.dmdemo.controllers.Credentials
import revenera.gcs.domain.entities.User
import revenera.gcs.domain.entities.DomainObject
import revenera.gcs.domain.entities.Session
import revenera.gcs.locking.Lockable
import revenera.gcs.locking.ReentrantLockingPolicy
import java.util.concurrent.ConcurrentHashMap

interface IUserManagement {
    fun getUsers() : Collection<User>

    fun locateUser(username: String) : User?

    fun locateUser(credentials: Credentials) : User?

    fun validateCredentials(credentials: Credentials) : User
}

interface ISessionManagement {
    fun getSessions() : Collection<Session>

    fun locateSession(sid: String) : Session?
}

interface IEntityManagement {
    fun injectEntity(entity: DomainObject) : DomainObject

    fun removeEntity(entity: DomainObject) : DomainObject?
}

@Service
class DomainManager(
    private val generator : StringGenerator,

    private val configuration: Configuration,

    private val filename: String = "cache.json") :
        Lockable(ReentrantLockingPolicy()), IUserManagement, ISessionManagement, IEntityManagement {

    private final val cache = ConcurrentHashMap<String, DomainObject>()

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }
    }

    @PostConstruct
    fun init() = locked {

        val file = configuration.getFile(filename)

        if (file.exists()) {
            val txt = file.readText()

            val objects = json.decodeFromString<DomainSnapshot>(txt)

            objects.credentials.forEach { credentials -> cache[credentials.id] = credentials }
            objects.sessions.forEach { session -> cache[session.userId] = session }
        }

        println("DomainManager initialized")
    }

    @PreDestroy
    fun cleanup() = locked {

        configuration.getFile(filename).writeText(
            json.encodeToString(DomainSnapshot(
                cache.values.filterIsInstance<User>(),
                cache.values.filterIsInstance<Session>()))
        )

        println("SessionManager being destroyed")
    }

    // IEntityManagement

    override fun injectEntity(entity: DomainObject) : DomainObject = locked {
        cache[entity.id] = entity

        entity
    }

    override fun removeEntity(entity: DomainObject) : DomainObject? = locked {
        cache.remove(entity.id)
    }

    // ISessionManagement

    override fun getSessions() : Collection<Session> = locked {
        cache.values.filterIsInstance<Session>()
    }

    override fun locateSession(sid: String) : Session? = locked {
        cache[sid] as Session?
    }

    // IUserManager
    override fun getUsers() : Collection<User> = locked {
        cache.values.filterIsInstance<User>()
    }

    override fun locateUser(username: String) : User? = locked {
        getUsers().find { user ->
            user.username == username
        }
    }

    override fun locateUser(credentials: Credentials) : User? =
        locateUser(credentials.username)

    override fun validateCredentials(credentials: Credentials) : User = locked {
        val user = locateUser(credentials) ?: throw UserNotFoundFault(credentials.username)

        if (user.password != credentials.password) {
            throw UserNotAuthenticatedFault(credentials.username)
        }

        user
    }
}