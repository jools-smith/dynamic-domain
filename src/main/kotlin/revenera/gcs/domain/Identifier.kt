package revenera.gcs.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface Identifier {
    val id: String
}
