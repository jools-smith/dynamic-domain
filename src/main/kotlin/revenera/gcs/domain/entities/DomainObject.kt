package revenera.gcs.domain.entities

import kotlinx.serialization.Serializable
import revenera.gcs.domain.DomainType


@Serializable
sealed interface DomainObject : DomainIdentifier {
    val type: DomainType
}