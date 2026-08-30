package revenera.gcs.dmdemo

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import revenera.gcs.dmdemo.model.Configuration
import revenera.gcs.dmdemo.model.StringGenerator
import revenera.gcs.dmdemo.model.SessionManager
import revenera.gcs.dmdemo.model.UserManager

@SpringBootApplication
@EnableScheduling
class DmDemoApplication (
    // beans
    private val sessionManager: SessionManager,
    // these are here for future use and clarity on what the singleton beans are
    private val userManager: UserManager,
    private val configuration: Configuration,
    private val stringGenerator: StringGenerator) {

    @PostConstruct
    fun init() {
        println("DmDemoApplication initialized")
    }

    @PreDestroy
    fun cleanup() {
        println("DmDemoApplication being destroyed")
    }

    @Scheduled(cron = "0 * * * * *")
    fun runEveryMinute(){
        sessionManager.removeExpiredSessions()
        println("Running every minute")
    }
}

fun main(args: Array<String>) {
    runApplication<DmDemoApplication>(*args)
}