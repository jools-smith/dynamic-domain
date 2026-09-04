package revenera.gcs.domain

import kotlinx.serialization.Serializable
import revenera.gcs.utils.StringGenerator

@Serializable
data class Transaction(
    override val id: String) : Entity {
    override val type: Type = Type.TRANSACTION

    companion object {
        fun create(generator: StringGenerator): Transaction =
            Transaction(generator.generate())
    }
}