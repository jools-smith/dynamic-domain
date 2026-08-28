package revenera.gcs.dmdemo

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@SpringBootApplication
@EnableScheduling
class DmDemoApplication {

    @PostConstruct
    fun init() {
        println("Bean initialized")
    }

    @PreDestroy
    fun cleanup() {
        println("Bean being destroyed")
    }

    @Scheduled(cron = "0 * * * * *")
    fun runEveryMinute(){
        println("Running every minute")
    }
}

fun main(args: Array<String>) {
    runApplication<DmDemoApplication>(*args)
}