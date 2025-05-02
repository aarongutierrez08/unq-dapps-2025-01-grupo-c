package com.example.unq_dapps_2025_01_grupo_c.controller

import Match
import com.example.unq_dapps_2025_01_grupo_c.dto.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.service.FootballDataService
import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/team")
class TeamController(
    private val footballDataService: FootballDataService,
    private val whoScoredService: WhoScoredService,
) {

    @GetMapping("/{teamName}/upcoming-matches")
    fun getUpcomingMatches(@PathVariable teamName: String): List<Match> {
        return footballDataService.getUpcomingMatchesByTeamName(teamName)
    }

    @PostMapping("/players")
    fun getPlayers(@Valid @RequestBody request: PlayerRequest): List<String> {
        return whoScoredService.fetchPlayers(request.team)
    }
}