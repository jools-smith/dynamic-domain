package revenera.gcs.dmdemo.model

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val name: String,
    val password: String
)