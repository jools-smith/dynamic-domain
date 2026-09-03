package revenera.gcs

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import revenera.gcs.utils.Loggable
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@Service
data class Configuration(
    val root : String = "c:\\working\\kotlin\\dm-demo\\storage") : Loggable() {

    fun getFilePath(vararg elements: String) : Path {
        return Paths.get(root, *elements).toAbsolutePath()
    }

    fun getFile(vararg elements: String) : File {
        return getFilePath(*elements).toFile()
    }

    @PostConstruct
    fun init() {

        File(root).mkdir()

        logger.info("initialized")
    }

    @PreDestroy
    fun cleanup() {
        logger.info("destroyed")
    }
}