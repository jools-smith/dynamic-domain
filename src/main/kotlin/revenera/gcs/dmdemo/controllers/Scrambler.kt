package revenera.gcs.dmdemo.controllers

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.controllers.SessionManager.Companion.filename
import revenera.gcs.dmdemo.controllers.SessionManager.Companion.json
import java.security.SecureRandom

@Service
class Scrambler() {
    private final val rand : SecureRandom = SecureRandom()


    companion object {
        const val BASE36 = "abcdefghijklmnopqrstuvwxyz0123456789"
        const val BASE34 = "abcdefghijklmnopqrstuvwxyz23456789"
    }

    @PostConstruct
    fun init() {
        println("Scrambler initialized")
    }

    @PreDestroy
    fun cleanup() {
        println("Scrambler being destroyed")
    }

    fun generateBase36Token(length: Int = 32): String {
        return generateToken(BASE36, length)
    }

    fun generateBase34Token(length: Int = 32): String {
        return generateToken(BASE34, length)
    }

    fun generateToken(charset: String, length: Int = 32): String {
        return buildString(length) {
            repeat(length) {
                append(charset[rand.nextInt(charset.length)])
            }
        }
    }
}