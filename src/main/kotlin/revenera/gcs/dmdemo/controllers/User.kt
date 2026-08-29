package revenera.gcs.dmdemo.controllers

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val credentials: Credentials
)