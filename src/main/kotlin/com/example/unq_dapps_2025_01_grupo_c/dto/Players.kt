package com.example.unq_dapps_2025_01_grupo_c.dto

import jakarta.validation.constraints.NotBlank

data class PlayerRequest(
    @field:NotBlank(message = "Team is required")
    val team: String,
)
