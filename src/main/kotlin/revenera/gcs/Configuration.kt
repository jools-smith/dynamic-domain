package revenera.gcs

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@Service
data class Configuration(
    val root : String = "c:\\working\\kotlin\\dm-demo\\storage") {

    fun getFilePath(vararg elements: String) : Path {
        return Paths.get(root, *elements).toAbsolutePath()
    }

    fun getFile(vararg elements: String) : File {
        return getFilePath(*elements).toFile()
    }

    @PostConstruct
    fun init() {

        File(root).mkdir()

        println("Configuration initialized")
    }
}