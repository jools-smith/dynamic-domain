package revenera.gcs.dmdemo.controllers

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import revenera.gcs.dmdemo.model.Configuration
import revenera.gcs.dmdemo.model.Credentials
import revenera.gcs.dmdemo.model.Session
import revenera.gcs.dmdemo.model.SessionFault
import revenera.gcs.dmdemo.model.SessionManager
import revenera.gcs.dmdemo.model.UserManager
import revenera.gcs.dmdemo.model.UserNotAuthenticatedFault


@RestController("sessionController", )
@RequestMapping("/api")
class SessionController(
    private val sessionManager: SessionManager,
    private val userManager: UserManager,
    private val configuration: Configuration) {

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

        val session = sessionManager.getSession(token)
            ?: throw SessionFault(
                HttpStatus.UNAUTHORIZED,
                "Session not found"
            )

        if (!session.isValid) {
            throw SessionFault(
                HttpStatus.UNAUTHORIZED,
                "Session has expired"
            )
        }

        return session
    }


    @GetMapping("/test")
    fun test(request: HttpServletRequest) : String = authenticated(request) { session ->

        println("Session: ${session.id}")

        "OK"
    }

    @GetMapping("/sessions")
    fun getSessions(request: HttpServletRequest): Any = authenticated(request) { session ->
        sessionManager.getSessions()
    }

    @PostMapping("/sessions/authenticate")
    fun createSession(@RequestBody credentials: Credentials): String {
        val user = userManager.validateUser(credentials) ?: throw UserNotAuthenticatedFault(credentials.name)

        return sessionManager.createSession(user.id)
    }

    @GetMapping("/users")
    fun getUsers(request: HttpServletRequest): Any = authenticated(request) { session ->
        userManager.getUsers()
    }

    @PostMapping("/users")
    fun createUser(@RequestBody credentials: Credentials,request: HttpServletRequest): ResponseEntity<String> = authenticated(request) { session ->

        val user = userManager.getUser(credentials.name)

        if (user != null) {
            ResponseEntity
                .ok(user.id)
        }
        else {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userManager.createUser(credentials))
        }
    }
}