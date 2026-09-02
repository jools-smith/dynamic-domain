package revenera.gcs.domain.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import revenera.gcs.domain.DomainType
import revenera.gcs.utils.StringGenerator
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Serializable
data class Session(
    override val id: String,
    val userId: String,
    val start: Instant = Clock.System.now(),
    val duration: Long) : DomainObject {
    override val type: DomainType = DomainType.SESSION

    companion object {
        fun create(
            generator: StringGenerator,
            credentials: User,
            duration: Duration = 1.hours): Session = Session(
                generator.generate(),
                credentials.id,
                duration = duration.inWholeSeconds)
    }

    @get:JsonIgnore
    val expires: Instant
        get() = start + duration.seconds

    @get:JsonIgnore
    val hasExpired: Boolean
        get() = Clock.System.now() > expires

    @get:JsonIgnore
    val isValid: Boolean
        get() = !hasExpired
}

