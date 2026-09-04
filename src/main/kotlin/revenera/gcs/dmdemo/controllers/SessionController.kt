package revenera.gcs.dmdemo.controllers

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import revenera.gcs.Configuration
import revenera.gcs.utils.StringGenerator
import revenera.gcs.SessionFault
import revenera.gcs.domain.Entity
import revenera.gcs.domain.Manager
import revenera.gcs.utils.Loggable


@RestController("sessionController", )
@RequestMapping("/api")
@Suppress("unused") //TODO:
class SessionController(
    private val manager: Manager,
    private val stringGenerator: StringGenerator,
    private val configuration: Configuration) : Loggable() {

    @PostConstruct
    fun init() {
        logger.info("initialized")
    }

    @PreDestroy
    fun cleanup() {
        logger.info("destroyed")
    }

    private fun <T> authenticated(
        request: HttpServletRequest,
        action: (Entity.Session) -> T): T {

        val session = validate(request)

        return action(session)
    }

    fun validate(request: HttpServletRequest): Entity.Session {
        val authorization = request.getHeader("Authorization")
            ?: throw SessionFault(
                HttpStatus.UNAUTHORIZED,
                "Missing session id"
            )

        val token = authorization
            .removePrefix("Bearer ")
            .trim()

        val session = manager.locateSession(token)
            ?: throw SessionFault(
                HttpStatus.UNAUTHORIZED,
                "Session not found"
            )

        if (session.hasExpired) {
            throw SessionFault(
                HttpStatus.UNAUTHORIZED,
                "Session has expired"
            )
        }

        return session
    }

    @GetMapping("/test")
    fun test(request: HttpServletRequest): Any =
        object {
            val sessions: Any = manager.getSessions()
            val admin: Any? = manager.locateUser("admin")
            val users: Any = manager.getUsers()
        }

    @GetMapping("/sessions")
    fun getSessions(request: HttpServletRequest): Any = authenticated(request) {
        manager.getSessions()
    }

    @PostMapping("/sessions/authenticate")
    fun createSession(@RequestBody credentials: Credentials): ResponseEntity<String> {

        val user = manager.validateCredentials(credentials)

        val session = Entity.Session.create(stringGenerator, user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(manager.injectEntity(session).id)
    }

    @GetMapping("/users")
    fun getUsers(request: HttpServletRequest): Any = authenticated(request) {
        manager.getUsers()
    }

    @PostMapping("/users")
    fun createUser(@RequestBody credentials: Credentials, request: HttpServletRequest): ResponseEntity<String> = authenticated(request) {

        val user = manager.locateUser(credentials)
        if (user != null) {
            ResponseEntity.ok(user.id)
        }
        else {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(manager.injectEntity(Entity.User.create(
                    stringGenerator,
                    credentials.username,
                    credentials.password)).id)
        }
    }

//    @GetMapping("/session/consume")
//    fun createUser(@RequestBody data: Any, request: HttpServletRequest): ResponseEntity<String> = authenticated(request) {
}

