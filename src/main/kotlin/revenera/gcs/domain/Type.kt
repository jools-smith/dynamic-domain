package revenera.gcs.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Type {
    @SerialName("SX")
    SESSION,

    @SerialName("UX")
    USER,

    @SerialName("TX")
    TRANSACTION
}