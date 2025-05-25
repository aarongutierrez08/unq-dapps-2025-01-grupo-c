package com.example.unq_dapps_2025_01_grupo_c.model.external

data class Area(
    val id: Int,
    val name: String,
    val code: String,
    val flag: String?
)

data class Competition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String?,
    val area: Area? = null
)

data class Season(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int?,
    val winner: Team? = null
)

data class Referee(
    val id: Int,
    val name: String,
    val type: String,
    val nationality: String?
)

data class Odds(
    val msg: String?,
    val homeWin: Double?,
    val draw: Double?,
    val awayWin: Double?
)