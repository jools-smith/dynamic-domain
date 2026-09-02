package revenera.gcs.domain.entities

import kotlinx.serialization.Serializable

@Serializable
sealed interface IIdentifier {
    val id: String
}
