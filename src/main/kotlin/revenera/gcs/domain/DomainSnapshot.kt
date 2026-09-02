package revenera.gcs.domain

import kotlinx.serialization.Serializable
import revenera.gcs.domain.entities.User
import revenera.gcs.domain.entities.Session

@Serializable
data class DomainSnapshot(
    val credentials: List<User>,
    val sessions: List<Session>,
)