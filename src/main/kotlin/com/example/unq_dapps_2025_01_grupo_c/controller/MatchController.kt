package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.match.MatchPredictionRequest
import com.example.unq_dapps_2025_01_grupo_c.dto.match.MatchPredictionResponse
import com.example.unq_dapps_2025_01_grupo_c.dto.match.UpcomingMatchesRequest
import com.example.unq_dapps_2025_01_grupo_c.model.external.Match
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.service.FootballDataService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/match")
@Tag(name = "Teams", description = "Information about football teams")
@Transactional
class MatchController(
    private val footballDataService: FootballDataService,
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
    @PostMapping("/upcoming-matches")
    fun getPlayers(
        @Parameter(description = "Name of the team", required = true)
        @Valid @RequestBody request: UpcomingMatchesRequest
    ): ResponseEntity<List<Match>> {
        return ResponseEntity.ok(footballDataService.getUpcomingMatchesByTeamName(request.teamName))
    }

    @Operation(
        summary = "Get match prediction",
        description = "Retrieve match prediction given two team names"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Match prediction info"),
            ApiResponse(
                responseCode = "404",
                description = "Any team not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/prediction")
    fun getMatchPrediction(
        @Parameter(description = "Names of the teams", required = true)
        @Valid @RequestBody request: MatchPredictionRequest
    ): ResponseEntity<MatchPredictionResponse> {
        return ResponseEntity.ok(footballDataService.getMatchPredictionTo(request.teamNameOne, request.teamNameTwo))
    }
}
