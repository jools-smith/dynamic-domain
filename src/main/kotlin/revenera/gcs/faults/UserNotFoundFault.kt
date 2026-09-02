package revenera.gcs.faults

import org.springframework.http.HttpStatus
import revenera.gcs.UserFault

class UserNotFoundFault (
    username: String
) : UserFault(username, HttpStatus.NOT_FOUND, reason = "does not exist")