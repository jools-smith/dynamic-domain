package revenera.gcs.dmdemo.domain

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.domain.session.Session
import revenera.gcs.dmdemo.domain.session.SessionFactory
import revenera.gcs.dmdemo.domain.user.User
import revenera.gcs.dmdemo.domain.user.UserFactory
import revenera.gcs.dmdemo.locking.Lockable
import revenera.gcs.dmdemo.locking.ReentrantLockingPolicy
import revenera.gcs.dmdemo.model.Configuration
import revenera.gcs.dmdemo.model.StringGenerator
import java.util.concurrent.ConcurrentHashMap

@Service
class DomainManager(
    private val stringGenerator: StringGenerator,
    private val configuration: Configuration,
    private val userFactory: UserFactory,
    private val sessionFactory: SessionFactory,
    private val filename: String = "cache.json") : Lockable(ReentrantLockingPolicy()) {

    private final val cache = ConcurrentHashMap<DomainIdentifier, IDomainObject>()

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

            objects.users.forEach { user -> cache[user.id] = user }
            objects.sessions.forEach { session -> cache[session.id] = session }
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

    fun injectEntity(entity: IDomainObject) = locked {
        cache[entity.id] = entity
    }

    fun removeEntity(id: DomainIdentifier) : IDomainObject? = locked {
        cache.remove(id)
    }
}