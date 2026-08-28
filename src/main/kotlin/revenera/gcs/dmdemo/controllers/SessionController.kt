package revenera.gcs.dmdemo.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("sessionController", )
@RequestMapping("/api/sessions")
class SessionController {

    @GetMapping
    fun hello(): String {
        return "Hello from Kotlin!"
    }
}