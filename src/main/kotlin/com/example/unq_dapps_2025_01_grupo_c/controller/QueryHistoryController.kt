package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.query_history.QueryHistoryResponse
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.exceptions.UserNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.model.query_history.toResponseList
import com.example.unq_dapps_2025_01_grupo_c.repository.QueryHistoryRepository
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/history")
@Tag(name = "QueryHistory", description = "Endpoints for logged user queries history")
@Transactional
class QueryHistoryController (
    private val queryHistoryRepository: QueryHistoryRepository,
    private val userRepository: UserRepository
) {
    @Operation(
        summary = "Get user queries history",
        description = "Retrieve logged user queries history"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Query history of logged user"),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @GetMapping("")
    fun getQueryHistory(): ResponseEntity<List<QueryHistoryResponse>> {
        val username = SecurityContextHolder.getContext().authentication.name
        val userId = userRepository.findByUsername(username)
            .orElseThrow { UserNotFoundException(username) }
            .id

        val queriesHistory = queryHistoryRepository.findAllByUserId(userId)

        return ResponseEntity.ok(queriesHistory.toResponseList())
    }
}

