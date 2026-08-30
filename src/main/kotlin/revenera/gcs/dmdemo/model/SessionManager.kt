package revenera.gcs.dmdemo.model

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.locking.Lockable
import revenera.gcs.dmdemo.locking.ReentrantLockingPolicy
import java.util.concurrent.ConcurrentHashMap


@Service
class SessionManager(
    private val stringGenerator: StringGenerator,
    private val configuration: Configuration
) : Lockable(ReentrantLockingPolicy()) {

    private final val sessions = ConcurrentHashMap<String, Session>()

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }
        private const val FILENAME = "sessions.json"
    }

    @PostConstruct
    fun init() = locked {

        val file = configuration.getFile(FILENAME)

        if (file.exists()) {
            val txt = configuration.getFile(FILENAME).readText()

            val savedSessions = json.decodeFromString<Map<String, Session>>(txt)

            sessions.putAll(savedSessions)
        }

        println("SessionManager initialized")
    }

    @PreDestroy
    fun cleanup() = locked {

        configuration.getFile(FILENAME).writeText(
            json.encodeToString(sessions.toMap())
        )

        println("SessionManager being destroyed")
    }

    fun createSession(userid: String): String = locked {
        val session = Session(
            stringGenerator.generateBase34Token(32),
            userId = userid
        )

        sessions[session.id] = session

        session.id
    }

    fun getSessions(): Collection<Session> = locked {
        sessions.values
    }

    fun getSession(sessionId: String): Session? = locked {
        sessions[sessionId]
    }

    fun removeExpiredSessions() = locked {

        sessions.entries.removeIf { (_, session) ->
            val expired = session.timeRemaining.isNegative()

            if (expired) {
                println("Removing expired session ${session.id} for user ${session.userId}")
            }

            expired
        }
    }
}