package revenera.gcs.dmdemo.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface IDomainObject {
    val id: DomainIdentifier
}