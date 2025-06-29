package com.example.unq_dapps_2025_01_grupo_c.dto.team

import jakarta.validation.constraints.NotBlank

data class TeamComparisonRequest(
    @field:NotBlank(message = "teamOne must not be blank")
    val teamOne: String,

    @field:NotBlank(message = "teamTwo must not be blank")
    val teamTwo: String
)

data class TeamStats(
    val name: String,
    val stats: Map<String, String>
)

data class TeamComparisonResponse(
    val teamOne: TeamStats,
    val teamTwo: TeamStats
)

data class TeamRequest(
    @field:NotBlank(message = "team must not be blank")
    val team: String
)

data class AdvancedMetricsResponse(
    val team: String,
    val goalPerGame: Double?,
    val cardsPerGame: Double?,
    val shotConversionRate: Double?,
    val passToGoalRatio: Double?,
    val disciplineScore: Double?,
    val averageRating: Double?,
    val rawStats: Map<String, String>
)
