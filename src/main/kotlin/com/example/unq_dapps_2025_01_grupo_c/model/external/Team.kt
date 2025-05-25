package com.example.unq_dapps_2025_01_grupo_c.model.external

data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String? = null,
    val squad: List<Squad>? = null
)

data class Squad(
    val id: String,
    val name: String,
    val position: String,
    val dateOfBirth: String?,
    val nationality: String
)

data class TeamsApiResponse(
    val teams: List<Team>
)