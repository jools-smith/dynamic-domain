package revenera.gcs.dmdemo.model

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class StringGenerator {
    private final val rand: SecureRandom = SecureRandom()


    companion object {
        private const val ALPHA = "abcdefghijklmnopqrstuvwxyz"
        private const val DIGITS = "0123456789"

        fun lowercase(): String = ALPHA
        fun uppercase(): String = ALPHA.uppercase()
        fun numeric(): String = DIGITS

        fun base36(): String = lowercase() + numeric()

        fun base34(): String = base36()
            .replace("i", "")
            .replace("o", "")

        fun bas60(): String = base36() + uppercase()

        fun bas56(): String = bas60()
            .replace("I", "")
            .replace("O", "")
            .replace("i", "")
            .replace("o", "")
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
        return generateToken(base36(), length)
    }

    fun generateBase34Token(length: Int = 32): String {
        return generateToken(base34(), length)
    }

    fun generateToken(charset: String, length: Int = 32): String {
        return buildString(length) {
            repeat(length) {
                append(charset[rand.nextInt(charset.length)])
            }
        }
    }
}