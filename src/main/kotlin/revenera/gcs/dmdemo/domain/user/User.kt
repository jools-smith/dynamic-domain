package revenera.gcs.dmdemo.domain.user

import kotlinx.serialization.Serializable
import revenera.gcs.dmdemo.domain.IDomainObject
import revenera.gcs.dmdemo.domain.DomainIdentifier
import revenera.gcs.dmdemo.model.Credentials

@Serializable
data class User(
    override val id: DomainIdentifier,
    val credentials: Credentials
) : IDomainObject

