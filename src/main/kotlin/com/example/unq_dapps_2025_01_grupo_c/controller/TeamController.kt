package com.example.unq_dapps_2025_01_grupo_c.controller

import Match
import com.example.unq_dapps_2025_01_grupo_c.service.FootballDataService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/team")
class FootballController(
    private val footballDataService: FootballDataService
) {

    @GetMapping("/{teamName}/upcoming-matches")
    fun getUpcomingMatches(@PathVariable teamName: String): List<Match> {
        return footballDataService.getUpcomingMatchesByTeamName(teamName)
    }
}