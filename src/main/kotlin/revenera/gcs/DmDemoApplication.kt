package revenera.gcs

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import revenera.gcs.dmdemo.controllers.Credentials
import revenera.gcs.domain.DomainManager
import revenera.gcs.domain.entities.User
import revenera.gcs.utils.Loggable
import revenera.gcs.utils.StringGenerator

@SpringBootApplication
@EnableScheduling
class DmDemoApplication (
    // beans
    private val domainManager: DomainManager,
    private val configuration: Configuration,
    private val stringGenerator: StringGenerator) : Loggable() {

    @PostConstruct
    fun init() {
        val credentials = Credentials("admin", "admin")

        val user = domainManager.locateUser(credentials)
        if (user == null) {
            domainManager.injectEntity(
                User.create(
                    stringGenerator,
                    credentials.username,
                    credentials.password))

            logger.debug("created {}", domainManager.locateUser(credentials))
        }
        else {
            logger.debug("located {}", user)
        }

        logger.info("initialized")
    }

    @PreDestroy
    fun cleanup() {
        logger.info("destroyed")
    }

    @Scheduled(cron = "\${app.housekeeping.cron}")
    fun housekeeping(){
        domainManager.housekeeping()

        logger.info("Running every minute")
    }
}

fun main(args: Array<String>) {
    runApplication<DmDemoApplication>(*args)
}