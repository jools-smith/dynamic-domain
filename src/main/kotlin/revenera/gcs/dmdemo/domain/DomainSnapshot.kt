package revenera.gcs.dmdemo.domain

import revenera.gcs.dmdemo.domain.session.Session
import revenera.gcs.dmdemo.domain.user.User

data class DomainSnapshot(
    val users: List<User>,
    val sessions: List<Session>,
)