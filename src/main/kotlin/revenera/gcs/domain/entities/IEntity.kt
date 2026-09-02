package revenera.gcs.domain.entities

import kotlinx.serialization.Serializable
import revenera.gcs.domain.DomainType


@Serializable
sealed interface IEntity : IIdentifier {
    val type: DomainType
}