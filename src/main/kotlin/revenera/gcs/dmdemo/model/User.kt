package revenera.gcs.dmdemo.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val credentials: Credentials
)