package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.dto.query_history.QueryHistoryResponse
import com.example.unq_dapps_2025_01_grupo_c.exceptions.UserNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.model.query_history.toResponseList
import com.example.unq_dapps_2025_01_grupo_c.repository.QueryHistoryRepository
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class QueryHistoryService(
    private val queryHistoryRepository: QueryHistoryRepository,
    private val userRepository: UserRepository
) {
    fun getUserQueryHistory(username: String): List<QueryHistoryResponse> {
        val userId = userRepository.findByUsername(username)
            .orElseThrow { UserNotFoundException(username) }
            .id

        val queriesHistory = queryHistoryRepository.findAllByUserId(userId)

        return queriesHistory.toResponseList()
    }
}
