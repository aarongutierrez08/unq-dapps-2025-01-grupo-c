package com.example.unq_dapps_2025_01_grupo_c.dto.player

import jakarta.validation.constraints.NotBlank

data class PlayerPerformanceRequest(
    @field:NotBlank(message = "playerName is required")
    val playerName: String,
)