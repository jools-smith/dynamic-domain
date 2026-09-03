package revenera.gcs.utils


import java.security.MessageDigest
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun String.md5(): String = hash("MD5")

@Suppress("unused") //TODO:
fun String.sha1(): String = hash("SHA-1")

@Suppress("unused") //TODO:
fun String.sha256(): String = hash("SHA-256")

fun String.hash(algorithm: String): String =
    MessageDigest.getInstance(algorithm)
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }

private val TIMESTAMP_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        .withZone(ZoneOffset.UTC)

private val ISO_OFFSET_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        .withZone(ZoneId.systemDefault())

@Suppress("unused") //TODO:
fun Instant.toTimestamp(): String =
    TIMESTAMP_FORMATTER.format(toJavaInstant())

@Suppress("unused") //TODO:
fun Instant.toZulu(): String =
    ISO_OFFSET_FORMATTER.format(toJavaInstant())