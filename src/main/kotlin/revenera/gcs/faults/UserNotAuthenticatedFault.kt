package revenera.gcs.faults

import org.springframework.http.HttpStatus
import revenera.gcs.UserFault

class UserNotAuthenticatedFault (
    username: String
) : UserFault(username, HttpStatus.UNAUTHORIZED, reason = "authentication failed")