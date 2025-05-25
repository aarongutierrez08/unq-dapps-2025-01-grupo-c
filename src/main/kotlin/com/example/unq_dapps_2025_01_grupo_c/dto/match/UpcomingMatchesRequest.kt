package com.example.unq_dapps_2025_01_grupo_c.dto.match

import jakarta.validation.constraints.NotBlank

data class UpcomingMatchesRequest(
    @field:NotBlank(message = "teamName is required")
    val teamName: String,
)