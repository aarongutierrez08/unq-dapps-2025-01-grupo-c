package com.example.unq_dapps_2025_01_grupo_c.dto.api

import jakarta.validation.constraints.NotBlank

data class PlayerPerformanceRequest(
    @field:NotBlank(message = "playerName is required")
    val playerName: String,
)

data class PlayerPerformanceResponse(
    val playerName: String,
    val position: String,
    val rating: Double,
)

data class PositionRating(
    val position: String,
    val rating: Double,
)