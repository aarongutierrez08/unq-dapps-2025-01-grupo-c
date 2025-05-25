package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.model.PlayerPerformanceRequest
import com.example.unq_dapps_2025_01_grupo_c.model.PlayerPerformanceResponse
import com.example.unq_dapps_2025_01_grupo_c.model.external.team.Team
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
@RequestMapping("/player")
@Tag(name = "Players", description = "Information about Premiere League players")
class PlayerController(
    private val footballDataService: FootballDataService,
    private val whoScoredService: WhoScoredService,
) {

    @Operation(
        summary = "Get player performance in Premiere League competition",
        description = "Retrieve player perfomance given a team name"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Player performance in Premiere League competition"),
            ApiResponse(
                responseCode = "404",
                description = "Player not found",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))]
            )
        ]
    )
    @PostMapping("/performance")
    fun getPlayers(
        @Parameter(description = "Name of the player", required = true)
        @Valid @RequestBody request: PlayerPerformanceRequest
    ): ResponseEntity<PlayerPerformanceResponse> {
        val playerTeam: Team = footballDataService.getTeamByNamePlayer(request.playerName)
        val playerBestPositionPerformance = whoScoredService.getPlayerPerformance(request.playerName, playerTeam.name)
        return ResponseEntity.ok(PlayerPerformanceResponse(request.playerName, playerBestPositionPerformance.position, playerBestPositionPerformance.rating))
    }
}