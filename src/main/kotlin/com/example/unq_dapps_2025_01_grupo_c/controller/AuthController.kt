package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.security.JwtUtil
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String)


@RestController
class AuthController(private val jwtUtil: JwtUtil) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): String {
        return jwtUtil.generateToken(request.username)
    }


}
