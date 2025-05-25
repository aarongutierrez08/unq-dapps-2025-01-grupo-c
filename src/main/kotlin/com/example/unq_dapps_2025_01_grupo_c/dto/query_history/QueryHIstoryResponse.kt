package com.example.unq_dapps_2025_01_grupo_c.dto.query_history

import java.time.LocalDateTime

data class QueryHistoryResponse(
    val term: String,
    val date: LocalDateTime,
    val userName: String
)