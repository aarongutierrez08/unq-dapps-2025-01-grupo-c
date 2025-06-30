package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.player.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredService
import io.micrometer.core.instrument.MeterRegistry
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/team")
@Tag(name = "Teams", description = "Information about football teams")
class TeamController(
    private val whoScoredService: WhoScoredService,
    private val meterRegistry: MeterRegistry
) {

    @Operation(
        summary = "Get players by team",
        description = "Retrieve a list of player names for a given team"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of player names retrieved successfully"),
            ApiResponse(
                responseCode = "404",
                description = "Team not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/players")
    fun getPlayers(
        @Parameter(description = "Player request containing team information", required = true)
        @Valid @RequestBody request: PlayerRequest
    ): ResponseEntity<List<String>> {
        meterRegistry
            .counter("app_teams_queries_total", "team", request.team)
            .increment()
        return ResponseEntity.ok(whoScoredService.fetchPlayers(request.team))
    }
}