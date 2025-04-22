package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredScraperService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WhoScoredScraperController(
    private val whoScoredScraperService: WhoScoredScraperService
) {

    @PostMapping("/players")
    fun getPlayers(@Valid @RequestBody request: PlayerRequest): List<String> {
        return whoScoredScraperService.fetchPlayers(request.team)
    }
}