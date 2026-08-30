package revenera.gcs.dmdemo.model

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

open class SessionFault (
    val status: HttpStatus,
    reason: String,
) : RuntimeException(reason)


open class UserFault (
    val username: String,
    val status: HttpStatus,
    reason: String,
) : RuntimeException(
    "User '$username' '$reason'")

class UserAlreadyExistsFault (
    username: String
) : UserFault(username, HttpStatus.CONFLICT, reason = "already exists")



class UserNotAuthenticatedFault (
    username: String
) : UserFault(username, HttpStatus.UNAUTHORIZED, reason = "authentication failed")

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(UserFault::class)
    fun handle(ex: UserFault): ProblemDetail =
        ProblemDetail.forStatusAndDetail(ex.status,ex.message)

    @ExceptionHandler(SessionFault::class)
    fun handle(ex: SessionFault): ProblemDetail =
        ProblemDetail.forStatusAndDetail(ex.status,ex.message)
}
