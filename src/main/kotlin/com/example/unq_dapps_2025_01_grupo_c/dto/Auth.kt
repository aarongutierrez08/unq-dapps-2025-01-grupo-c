package com.example.unq_dapps_2025_01_grupo_c.dto

data class AuthRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val message: String
)
