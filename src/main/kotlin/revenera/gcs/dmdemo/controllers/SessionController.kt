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
import revenera.gcs.domain.DomainManager
import revenera.gcs.domain.entities.Session
import revenera.gcs.domain.entities.User
import revenera.gcs.utils.Loggable


@RestController("sessionController", )
@RequestMapping("/api")
class SessionController(
    private val domainManager: DomainManager,
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
        action: (Session) -> T): T {

        val session = validate(request)

        return action(session)
    }

    fun validate(request: HttpServletRequest): Session {
        val authorization = request.getHeader("Authorization")
            ?: throw SessionFault(
                HttpStatus.UNAUTHORIZED,
                "Missing session id"
            )

        val token = authorization
            .removePrefix("Bearer ")
            .trim()

//        println(token)

        val session = domainManager.locateSession(token)
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
            val sessions: Any = domainManager.getSessions()
            val admin: Any? = domainManager.locateUser("admin")
            val users: Any = domainManager.getUsers()
//            val cache: Any = domainManager.cache
        }

    @GetMapping("/sessions")
    fun getSessions(request: HttpServletRequest): Any = authenticated(request) {
        domainManager.getSessions()
    }

    @PostMapping("/sessions/authenticate")
    fun createSession(@RequestBody credentials: Credentials): ResponseEntity<String> {

//        println("{$credentials}")

        val user = domainManager.validateCredentials(credentials)

        val session = Session.create(stringGenerator, user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(domainManager.injectEntity(session).id)
    }

    @GetMapping("/users")
    fun getUsers(request: HttpServletRequest): Any = authenticated(request) {
        domainManager.getUsers()
    }

    @PostMapping("/users")
    fun createUser(@RequestBody credentials: Credentials, request: HttpServletRequest): ResponseEntity<String> = authenticated(request) {

        val user = domainManager.locateUser(credentials)
        if (user != null) {
            ResponseEntity.ok(user.id)
        }
        else {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(domainManager.injectEntity(User.create(
                    stringGenerator,
                    credentials.username,
                    credentials.password)).id)
        }
    }
}

