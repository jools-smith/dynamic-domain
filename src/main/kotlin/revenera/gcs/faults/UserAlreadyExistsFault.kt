package revenera.gcs.faults

import org.springframework.http.HttpStatus
import revenera.gcs.UserFault

class UserAlreadyExistsFault (
    username: String
) : UserFault(username, HttpStatus.CONFLICT, reason = "already exists")