package revenera.gcs.domain.entities

import kotlinx.serialization.Serializable
import revenera.gcs.domain.DomainType
import revenera.gcs.StringGenerator

@Serializable
data class User(
    override val id: String,
    val username: String,
    val password: String) : DomainObject {
    override val type: DomainType = DomainType.USER

    companion object {
        fun create(
            generator: StringGenerator,
            username: String,
            password: String) = User(
            generator.generate(),
            username,
            password
        )
    }
}


