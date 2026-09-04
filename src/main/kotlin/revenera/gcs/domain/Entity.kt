package revenera.gcs.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import revenera.gcs.utils.StringGenerator
import revenera.gcs.utils.md5
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant


@Serializable
sealed interface Entity : Identifier {
    val type: Type

    @Serializable
    enum class Type {
        @SerialName("SX")
        SESSION,

        @SerialName("UX")
        USER,

        @SerialName("TX")
        TRANSACTION
    }

    @Serializable
    @Suppress("unused") //TODO:
    data class Session(
        override val id: String,
        val userId: String,
        val start: Instant = Clock.System.now(),
        val duration: Long) : Entity {
        override val type: Type = Type.SESSION

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
            get() = this.start + this.duration.seconds

        @get:JsonIgnore
        val hasExpired: Boolean
            get() = Clock.System.now() > this.expires

        @get:JsonIgnore
        val isValid: Boolean
            get() = !this.hasExpired
    }


    @Serializable
    data class User(
        override val id: String,
        val username: String,
        val password: String,
        val hash: String) : Entity {
        override val type: Type = Type.USER

        companion object {
            fun create(
                generator: StringGenerator,
                username: String,
                password: String) = User(
                generator.generate(),
                username,
                password,
                password.md5()
            )
        }
    }

    @Serializable
    data class Transaction(
        override val id: String) : Entity {
        override val type: Type = Type.TRANSACTION

        companion object {
            fun create(generator: StringGenerator): Transaction =
                Transaction(generator.generate())
        }
    }
}