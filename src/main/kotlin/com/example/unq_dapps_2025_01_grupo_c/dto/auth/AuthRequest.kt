package com.example.unq_dapps_2025_01_grupo_c.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequest(
    @field:NotBlank(message = "Username must not be empty")
    @field:Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    val username: String,

    @field:NotBlank(message = "Password must not be empty")
    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String
)