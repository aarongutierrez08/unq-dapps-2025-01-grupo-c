package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.AuthRequest
import com.example.unq_dapps_2025_01_grupo_c.dto.AuthResponse
import com.example.unq_dapps_2025_01_grupo_c.service.AuthService
import com.example.unq_dapps_2025_01_grupo_c.utils.Validator
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val validator: Validator
) {
    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val validationMessage = validator.validateRegister(request.username, request.password )
        if (validationMessage != "OK") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AuthResponse(validationMessage, error = "VALIDATION_ERROR"))
        }

        return try {
            val token = authService.register(request)
            ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(AuthResponse("Usuario registrado exitosamente", token))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AuthResponse("Error al registrar usuario", error = e.message))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val validationMessage = validator.validateLogin(request.username, request.password)
        if (validationMessage != "OK") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AuthResponse(validationMessage, error = "VALIDATION_ERROR"))
        }

        return try {
            val token = authService.login(request)
            ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(AuthResponse("Inicio de sesión exitoso", token))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AuthResponse("Error al iniciar sesión", error = e.message))
        }
    }
}
