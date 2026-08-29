package revenera.gcs.dmdemo

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import revenera.gcs.dmdemo.controllers.Configuration
import revenera.gcs.dmdemo.controllers.Scrambler
import revenera.gcs.dmdemo.controllers.SessionManager
import revenera.gcs.dmdemo.controllers.UserManager

@SpringBootApplication
@EnableScheduling
class DmDemoApplication (
    // beans
    private val sessionManager: SessionManager,
    private val userManager: UserManager,
    private val configuration: Configuration,
    private val scrambler: Scrambler) {

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