package revenera.gcs.dmdemo.controllers

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    val username: String,
    val password: String)