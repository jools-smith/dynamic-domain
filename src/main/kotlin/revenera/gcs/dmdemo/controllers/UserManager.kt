package revenera.gcs.dmdemo.controllers

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.UserAlreadyExistsFault
import java.util.concurrent.ConcurrentHashMap

@Service
class UserManager(
    private val scrambler: Scrambler,
    private val configuration: Configuration) {

    private final val users = ConcurrentHashMap<String, User>()

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }
        val filename = "users.json"
    }

    @PostConstruct
    fun init() {

        val file = configuration.getFile(filename)

        if (file.exists()) {
            val txt = configuration.getFile(filename).readText()

            val savedUsers = json.decodeFromString<Map<String, User>>(txt)

            users.putAll(savedUsers)
        }

        println("UserManager initialized")
    }

    @PreDestroy
    fun cleanup() {

        configuration.getFile(filename).writeText(
            json.encodeToString(users.toMap())
        )

        println("UserManager being destroyed")
    }

    fun getUsers() : List<User> {
        return users.values.toList()
    }

    fun createUser(credentials: Credentials) : String {

        val user = users.entries.firstOrNull {
            it.value.credentials.name == credentials.name
        }

        if (user != null) {
            throw UserAlreadyExistsFault(credentials.name)
        }

        val newUser = User(
            scrambler.generateBase36Token(32),
            credentials)

        users[newUser.id] = newUser

        return newUser.id
    }

    fun validateUser(credentials: Credentials): User? {
        return users.entries.find {
            it.value.credentials.name == credentials.name && it.value.credentials.password == credentials.password
        }?.value
    }

    fun getUser(username: String): User? {
        return users.entries.find {
            it.value.credentials.name == username
        }?.value
    }
}