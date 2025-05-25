package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.auth.AuthRequest
import com.example.unq_dapps_2025_01_grupo_c.dto.auth.AuthResponse
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
class AuthController(
    private val authService: AuthService
) {

    @Operation(
        summary = "Register a new user",
        description = "Registers a new user and returns an authorization token"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User registered successfully"),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request format or user already exists",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/register")
    fun register(
        @Parameter(description = "User registration details", required = true)
        @Valid @RequestBody request: AuthRequest
    ): ResponseEntity<AuthResponse> {
        val token = authService.register(request)
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(AuthResponse("User registered"))
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates an existing user and returns an authorization token"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful"),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid credentials",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Invalid request format",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/login")
    fun login(
        @Parameter(description = "User login details", required = true)
        @Valid @RequestBody request: AuthRequest
    ): ResponseEntity<AuthResponse> {
        val token = authService.login(request)
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .body(AuthResponse("Login successful"))
    }
}
