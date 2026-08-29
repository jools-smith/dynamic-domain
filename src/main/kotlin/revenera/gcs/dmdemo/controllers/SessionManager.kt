package revenera.gcs.dmdemo.controllers

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Clock

@Service
class SessionManager(
    private val scrambler: Scrambler,
    private val configuration: Configuration) {

    private final val sessions = ConcurrentHashMap<String, Session>()

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }
        val filename = "sessions.json"
    }

    @PostConstruct
    fun init() {

        val file = configuration.getFile(filename)

        if (file.exists()) {
            val txt = configuration.getFile(filename).readText()

            val savedSessions = json.decodeFromString<Map<String, Session>>(txt)

            sessions.putAll(savedSessions)
        }

        println("SessionManager initialized")
    }

    @PreDestroy
    fun cleanup() {

        configuration.getFile(filename).writeText(
            json.encodeToString(sessions.toMap())
        )

        println("SessionManager being destroyed")
    }

    fun createSession(userid: String): String {
        val session = Session(
            scrambler.generateBase34Token(32),
            userId = userid
        )

        sessions[session.id] = session

        return session.id
    }

    fun getSessions(): Collection<Session> {
        return sessions.values
    }

    fun getSession(sessionId: String): Session? {
        return sessions[sessionId]
    }

    fun removeExpiredSessions() {
        val now = Clock.System.now().toEpochMilliseconds()

        sessions.entries.removeIf { (_, session) ->
            val expired = session.timeRemaining.isNegative()

            if (expired) {
                println("Removing expired session ${session.id} for user ${session.userId}")
            }

            expired
        }
    }
}