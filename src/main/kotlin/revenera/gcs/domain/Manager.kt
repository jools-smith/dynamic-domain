package revenera.gcs.domain

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.Configuration
import revenera.gcs.dmdemo.controllers.Credentials
import revenera.gcs.faults.UserNotAuthenticatedFault
import revenera.gcs.faults.UserNotFoundFault
import revenera.gcs.locking.Lockable
import revenera.gcs.locking.ReentrantLockingPolicy
import revenera.gcs.utils.StringGenerator
import java.util.concurrent.ConcurrentHashMap

@Service
@Suppress("unused") //TODO:
class Manager(
    private val generator : StringGenerator,
    private val configuration: Configuration) :
        Lockable(ReentrantLockingPolicy()), IUserManagement, ISessionManagement, IEntityManagement {

    /** used for serialization only */
    @Serializable
    private data class Snapshot(
        val credentials: List<Entity.User>,
        val sessions: List<Entity.Session>,
    )

    //TODO: encapsulate cache against IEntityManagement
    private final val cache = ConcurrentHashMap<String, Entity>()

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

            val objects = json.decodeFromString<Snapshot>(txt)

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
            json.encodeToString(Snapshot(
                cache.values.filterIsInstance<Entity.User>(),
                cache.values.filterIsInstance<Entity.Session>()))
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

    override fun injectEntity(entity: Entity) : Entity = locked {
        cache[entity.id] = entity

        entity
    }

    override fun removeEntity(entity: Entity) : Entity? = locked {
        cache.remove(entity.id)
    }

    // ISessionManagement

    override fun getSessions() : Collection<Entity.Session> = locked {
        cache.values.filterIsInstance<Entity.Session>()
    }

    override fun locateSession(sid: String) : Entity.Session? = locked {
        cache[sid] as Entity.Session?
    }

    // IUserManager
    override fun getUsers() : Collection<Entity.User> = locked {
        cache.values.filterIsInstance<Entity.User>()
    }

    override fun locateUser(username: String) : Entity.User? = locked {
        getUsers().find { user ->
            user.username == username
        }
    }

    override fun locateUser(credentials: Credentials) : Entity.User? =
        locateUser(credentials.username)

    override fun validateCredentials(credentials: Credentials) : Entity.User = locked {
        val user = locateUser(credentials) ?: throw UserNotFoundFault(credentials.username)

        if (user.password != credentials.password) {
            throw UserNotAuthenticatedFault(credentials.username)
        }

        user
    }
}