package com.example.unq_dapps_2025_01_grupo_c.controller

import Match
import com.example.unq_dapps_2025_01_grupo_c.dto.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.service.FootballDataService
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
@Tag(name = "Teams", description = "Information about football teams")
class TeamController(
    private val footballDataService: FootballDataService,
    private val whoScoredService: WhoScoredService,
) {

    @Operation(
        summary = "Get upcoming matches by team name",
        description = "Retrieve upcoming matches given a team name"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "List of upcoming matches"),
            ApiResponse(
                responseCode = "404",
                description = "Team not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @GetMapping("/{teamName}/upcoming-matches")
    fun getUpcomingMatches(
        @Parameter(description = "Name of the team", required = true)
        @PathVariable teamName: String
    ): ResponseEntity<List<Match>> {
        return ResponseEntity.ok(footballDataService.getUpcomingMatchesByTeamName(teamName))
    }

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
}