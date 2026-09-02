package revenera.gcs

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import revenera.gcs.domain.DomainManager
import revenera.gcs.domain.entities.User
import revenera.gcs.utils.StringGenerator

@SpringBootApplication
@EnableScheduling
class DmDemoApplication (
    // beans
    private val domainManager: DomainManager,
    private val configuration: Configuration,
    private val stringGenerator: StringGenerator) {

    @PostConstruct
    fun init() {
        if (domainManager.getUsers().isEmpty()) {
            domainManager.injectEntity(User.create(stringGenerator, "admin", "admin"))
        }
        println("DmDemoApplication initialized")
    }

    @PreDestroy
    fun cleanup() {
        println("DmDemoApplication being destroyed")
    }

    @Scheduled(cron = "0 * * * * *")
    fun runEveryMinute(){
//        sessionManager.removeExpiredSessions()
        println("Running every minute")
    }
}

fun main(args: Array<String>) {
    runApplication<DmDemoApplication>(*args)
}