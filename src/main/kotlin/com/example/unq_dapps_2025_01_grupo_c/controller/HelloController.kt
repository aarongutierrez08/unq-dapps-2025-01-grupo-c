package com.example.unq_dapps_2025_01_grupo_c.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/")
    fun hello() = "Hello from Kotlin Spring Boot!"
}
