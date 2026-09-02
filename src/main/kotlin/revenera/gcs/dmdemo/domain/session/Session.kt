package revenera.gcs.dmdemo.domain.session

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import revenera.gcs.dmdemo.domain.IDomainObject
import revenera.gcs.dmdemo.domain.DomainIdentifier
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@Serializable
data class Session(
    override val id: DomainIdentifier,
    val userId: DomainIdentifier,
    val lifetime: Duration = 1.hours,
    val created: Long = Clock.System.now().toEpochMilliseconds()
) : IDomainObject {
    @get:JsonIgnore
    val expiration: Long
        get() = created + lifetime.inWholeMilliseconds

    @get:JsonIgnore
    val expirationTime: Instant
        get() = Instant.fromEpochMilliseconds(expiration)

    @get:JsonIgnore
    val timeRemaining: Duration
        get() = expirationTime - Clock.System.now()

    @get:JsonIgnore
    val isValid: Boolean
        get() = timeRemaining > Duration.ZERO
}


