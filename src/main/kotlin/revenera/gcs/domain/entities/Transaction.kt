package revenera.gcs.domain.entities

import kotlinx.serialization.Serializable
import revenera.gcs.utils.StringGenerator
import revenera.gcs.domain.DomainType

@Serializable
data class Transaction(
    override val id: String) : IEntity {
    override val type: DomainType = DomainType.TRANSACTION

    companion object {
        fun create(generator: StringGenerator): Transaction =
            Transaction(generator.generate())
    }
}