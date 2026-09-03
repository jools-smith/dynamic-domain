package revenera.gcs.domain

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.Configuration
import revenera.gcs.dmdemo.controllers.Credentials
import revenera.gcs.domain.entities.IEntity
import revenera.gcs.domain.entities.Session
import revenera.gcs.domain.entities.User
import revenera.gcs.faults.UserNotAuthenticatedFault
import revenera.gcs.faults.UserNotFoundFault
import revenera.gcs.locking.Lockable
import revenera.gcs.locking.ReentrantLockingPolicy
import revenera.gcs.utils.StringGenerator
import java.util.concurrent.ConcurrentHashMap

@Service
class DomainManager(
    private val generator : StringGenerator,
    private val configuration: Configuration) :
        Lockable(ReentrantLockingPolicy()), IUserManagement, ISessionManagement, IEntityManagement {

    //TODO: encapsulate cache against IEntityManagement
    private final val cache = ConcurrentHashMap<String, IEntity>()

    val userManager: IUserManagement
        get() = this

    val sessionManager: ISessionManagement
        get() = this

    val entityManager: IEntityManagement
        get() = this

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }

        private const val FILENAME: String = "cache.json"
    }

    private fun cacheLoad(): Boolean {
        val file = configuration.getFile(FILENAME)

        if (file.exists()) {
            val txt = file.readText()

            val objects = json.decodeFromString<DomainSnapshot>(txt)

            objects.credentials.forEach { credentials -> cache[credentials.id] = credentials }
            objects.sessions.forEach { session -> cache[session.id] = session }

            logger.debug("cache loaded")
            return true;
        }
        else {
            return false
        }
    }

    private fun cacheSerialize() {
        configuration.getFile(FILENAME).writeText(
            json.encodeToString(DomainSnapshot(
                cache.values.filterIsInstance<User>(),
                cache.values.filterIsInstance<Session>()))
        )
        logger.debug("cache serialized")
    }

    @PostConstruct
    fun init() = locked {
        cacheLoad()

        logger.info("initialized")
    }

    @PreDestroy
    fun cleanup() = locked {
        cacheSerialize()

        logger.info("destroyed")
    }

    fun housekeeping() = locked {
        cacheSerialize()
    }

    // IEntityManagement

    override fun injectEntity(entity: IEntity) : IEntity = locked {
        cache[entity.id] = entity

        entity
    }

    override fun removeEntity(entity: IEntity) : IEntity? = locked {
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