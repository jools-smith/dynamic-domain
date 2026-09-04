package revenera.gcs.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import revenera.gcs.utils.StringGenerator
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant


@Serializable
sealed interface Entity : Identifier {
    val type: Type
}