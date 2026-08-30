package revenera.gcs.dmdemo.model

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.locking.Lockable
import revenera.gcs.dmdemo.locking.ReentrantLockingPolicy
import java.util.concurrent.ConcurrentHashMap

@Service
class UserManager(
    private val stringGenerator: StringGenerator,
    private val configuration: Configuration
) : Lockable(ReentrantLockingPolicy()) {

    private final val users = ConcurrentHashMap<String, User>()

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }
        const val FILENAME = "users.json"
    }

    @PostConstruct
    fun init() {

        val file = configuration.getFile(FILENAME)

        if (file.exists()) {
            val txt = configuration.getFile(FILENAME).readText()

            val savedUsers = json.decodeFromString<Map<String, User>>(txt)

            users.putAll(savedUsers)
        }

        println("UserManager initialized")
    }

    @PreDestroy
    fun cleanup() = locked {

        configuration.getFile(FILENAME).writeText(
            json.encodeToString(users.toMap())
        )

        println("UserManager being destroyed")
    }

    fun getUsers(): List<User> = locked {
        users.values.toList()
    }

    fun createUser(credentials: Credentials): String = locked {

        val user = users.entries.firstOrNull {
            it.value.credentials.name == credentials.name
        }

        if (user != null) {
            throw UserAlreadyExistsFault(credentials.name)
        }

        val newUser = User(
            stringGenerator.generateBase36Token(32),
            credentials
        )

        users[newUser.id] = newUser

        newUser.id
    }

    fun validateUser(credentials: Credentials): User? = locked {
        users.entries.find {
            it.value.credentials.name == credentials.name && it.value.credentials.password == credentials.password
        }?.value
    }

    fun getUser(username: String): User? = locked {
        users.entries.find {
            it.value.credentials.name == username
        }?.value
    }
}