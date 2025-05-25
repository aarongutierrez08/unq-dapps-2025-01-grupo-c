package com.example.unq_dapps_2025_01_grupo_c.repository

import com.example.unq_dapps_2025_01_grupo_c.model.query_history.QueryHistory
import org.springframework.data.jpa.repository.JpaRepository

interface QueryHistoryRepository : JpaRepository<QueryHistory, Long> {
    fun findAllByUserId(userId: Long): List<QueryHistory>
}