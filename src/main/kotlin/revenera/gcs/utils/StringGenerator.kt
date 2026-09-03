package revenera.gcs.utils

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class StringGenerator(
    val charset:String = StringGenerator.bas56(),
    val length:Int = 32) : Loggable() {

    private val rand = SecureRandom()

    @PostConstruct
    fun init() {
        logger.info("initialized")
    }

    @PreDestroy
    fun cleanup() {
        logger.info("destroyed")
    }

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


    fun generate(): String = generate(charset, length)

    fun generate(charset: String, length: Int): String =
        buildString(length) {
            repeat(length) {
                append(charset[rand.nextInt(charset.length)])
            }
        }

    fun generate(
        charset: String,
        separator: String,
        groupSize: Int,
        numGroups: Int): String = (1..numGroups).joinToString(separator) {
            generate(charset, groupSize)
        }

}