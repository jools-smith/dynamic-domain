package revenera.gcs.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DomainType {
    @SerialName("SX")
    SESSION,

    @SerialName("UX")
    USER,

    @SerialName("TX")
    TRANSACTION
}