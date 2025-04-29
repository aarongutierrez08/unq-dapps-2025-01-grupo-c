package com.example.unq_dapps_2025_01_grupo_c.dto

import java.time.LocalDateTime // O el tipo que prefieras para la fecha

data class MatchDTO(
    val homeTeam: String,
    val awayTeam: String,
    val date: LocalDateTime, // O String, etc.
    val competition: String
    // Puedes añadir más campos si los necesitas (ej: estadio, resultado si ya se jugó, etc.)
)