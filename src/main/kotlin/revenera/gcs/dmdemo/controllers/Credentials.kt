package revenera.gcs.dmdemo.controllers

import kotlinx.serialization.Serializable
import revenera.gcs.domain.DomainType
import revenera.gcs.domain.entities.DomainObject

@Serializable
data class Credentials(
    val username: String,
    val password: String)