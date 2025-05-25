package com.example.unq_dapps_2025_01_grupo_c.dto.match

import java.time.ZonedDateTime

data class MatchPredictionResponse(
    val prediction: String,
    val confidence: Double,
    val expectedScore: ExpectedScore?,
    val historicalResults: List<HistoricalResult>
)

data class ExpectedScore(
    val teamOne: Int,
    val teamTwo: Int
)

data class HistoricalResult(
    val date: ZonedDateTime,
    val teamOne: String,
    val teamTwo: String,
    val score: String,
    val winner: String
)