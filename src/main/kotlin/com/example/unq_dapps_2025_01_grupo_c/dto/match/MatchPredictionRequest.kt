package com.example.unq_dapps_2025_01_grupo_c.dto.match

import jakarta.validation.constraints.NotBlank

data class MatchPredictionRequest(
    @field:NotBlank(message = "teamNameOne is required")
    val teamNameOne: String,
    @field:NotBlank(message = "teamNameTwo is required")
    val teamNameTwo: String,
)