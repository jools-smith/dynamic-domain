package revenera.gcs.dmdemo.domain

import kotlinx.serialization.Serializable

@Serializable
enum class DomainObjectType {
    USER,
    SERVICE,
    SESSION,
    POLICY,
    RATE_TABLE
}