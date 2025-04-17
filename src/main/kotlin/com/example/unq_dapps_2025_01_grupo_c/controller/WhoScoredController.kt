package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredScraperService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class TeamRequest(val team: String)

@RestController
class WhoScoredScraperController(
    private val scraperService: WhoScoredScraperService
) {

    @PostMapping("/players")
    fun getPlayers(@RequestBody request: TeamRequest): List<String> {
        return scraperService.fetchPlayers(request.team)
    }
}

