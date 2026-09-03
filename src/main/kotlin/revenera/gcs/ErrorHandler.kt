package revenera.gcs

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.security.SecureRandom

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

@Suppress("unused") //TODO:
@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(UserFault::class)
    fun handle(ex: UserFault): ProblemDetail =
        ProblemDetail.forStatusAndDetail(ex.status,ex.message)

    @ExceptionHandler(SessionFault::class)
    fun handle(ex: SessionFault): ProblemDetail =
        ProblemDetail.forStatusAndDetail(ex.status,ex.message)


    @ExceptionHandler(NotImplementedError::class)
    fun handle(ex: NotImplementedError): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,ex.message)
}
