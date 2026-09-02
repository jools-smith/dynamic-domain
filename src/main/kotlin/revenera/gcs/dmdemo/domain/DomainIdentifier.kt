package revenera.gcs.dmdemo.domain

import kotlinx.serialization.Serializable

@Serializable
data class DomainIdentifier(
    val type: DomainObjectType,
    val id: String,
    val name: String = ""
)