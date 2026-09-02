package revenera.gcs.domain.entities

import kotlinx.serialization.Serializable

@Serializable
sealed interface DomainIdentifier {
    val id: String
}
