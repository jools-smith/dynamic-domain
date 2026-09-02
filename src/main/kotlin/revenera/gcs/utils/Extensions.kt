package revenera.gcs.utils


import java.security.MessageDigest

fun String.md5(): String = hash("MD5")

fun String.sha1(): String = hash("SHA-1")

fun String.sha256(): String = hash("SHA-256")

fun String.hash(algorithm: String): String =
    MessageDigest.getInstance(algorithm)
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }