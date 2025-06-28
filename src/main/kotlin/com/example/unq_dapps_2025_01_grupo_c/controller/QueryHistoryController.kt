package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.query_history.QueryHistoryResponse
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.service.QueryHistoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/history")
@Tag(name = "QueryHistory", description = "Endpoints for logged user queries history")
class QueryHistoryController (
    private val queryHistoryService: QueryHistoryService
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
        val queriesHistory = queryHistoryService.getUserQueryHistory(username)
        return ResponseEntity.ok(queriesHistory)
    }
}
