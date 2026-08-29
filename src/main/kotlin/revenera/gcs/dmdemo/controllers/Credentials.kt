package revenera.gcs.dmdemo.controllers

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val name: String,
    val password: String
)