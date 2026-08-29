package revenera.gcs.dmdemo.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import revenera.gcs.dmdemo.UserNotAuthenticatedFault
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import revenera.gcs.dmdemo.SessionFault


@RestController("sessionController", )
@RequestMapping("/api")
class SessionController(
    private val sessionManager: SessionManager,
    private val userManager: UserManager,
    private val configuration: Configuration) {


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
    fun getUsers(request: HttpServletRequest) : String {

        val session = validate(request)

        println("Session: ${session.id}")

        return "OK"
    }

    @GetMapping("/sessions")
    fun getSessions(): Any {
        return sessionManager.getSessions()
    }

    @PostMapping("/sessions/authenticate")
    fun createSession(@RequestBody credentials: Credentials): String {
        val user = userManager.validateUser(credentials) ?: throw UserNotAuthenticatedFault(credentials.name)

        return sessionManager.createSession(user.id)
    }

    @GetMapping("/users")
    fun getUsers(): Any {
        return userManager.getUsers()
    }

    @PostMapping("/users")
    fun createUser(@RequestBody credentials: Credentials): ResponseEntity<String> {

        val user = userManager.getUser(credentials.name)

        return if (user != null) {
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