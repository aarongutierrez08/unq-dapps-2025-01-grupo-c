package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.player.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.dto.team.*
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredService
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
@Tag(name = "Teams", description = "Information about Premiere League football teams")
class TeamController(
    private val whoScoredService: WhoScoredService,
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
        return ResponseEntity.ok(whoScoredService.fetchPlayers(request.team))
    }

    @Operation(
        summary = "Compare statistics of two teams",
        description = "Returns the total/average statistics of both teams for comparison."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Comparison completed successfully"),
            ApiResponse(
                responseCode = "404",
                description = "Team not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/compare")
    fun compareTeams(@RequestBody request: TeamComparisonRequest): ResponseEntity<TeamComparisonResponse> {
        val (stats1, stats2) = whoScoredService.fetchTeamsStats(request.teamOne, request.teamTwo)
        val response = TeamComparisonResponse(
            teamOne = TeamStats(name = request.teamOne, stats = stats1),
            teamTwo = TeamStats(name = request.teamTwo, stats = stats2)
        )
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Get advanced metrics for a team",
        description = "Returns advanced custom-calculated metrics for a team using scrapped statistics."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Advanced metrics calculated successfully"),
            ApiResponse(
                responseCode = "404",
                description = "Team not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/advanced-metrics")
    fun getAdvancedMetrics(
        @RequestBody request: TeamRequest
    ): ResponseEntity<AdvancedMetricsResponse> {
        return ResponseEntity.ok(whoScoredService.fetchAdvancedMetrics(request.team))
    }

}