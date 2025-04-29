package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.MatchDTO
import com.example.unq_dapps_2025_01_grupo_c.dto.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredScraperService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
class WhoScoredScraperController(
    private val whoScoredScraperService: WhoScoredScraperService
) {

    @PostMapping("/players")
    fun getPlayers(@Valid @RequestBody request: PlayerRequest): List<String> {
        return whoScoredScraperService.fetchPlayers(request.team)
    }
    /**
     * Endpoint para obtener los próximos 5 partidos de un equipo.
     * @param teamName El nombre del equipo (extraído de la URL).
     * @return Una lista de DTOs representando los próximos 5 partidos.
     */
    @GetMapping("/teams/{teamName}/next-matches") // Usamos GET y una variable en la ruta
    fun getNextMatches(@PathVariable teamName: String): List<MatchDTO> {
        // Asumimos que el servicio tiene un metodo fetchNextMatches
        // que devuelve los próximos 5 partidos.
        return whoScoredScraperService.fetchNextMatches(teamName)
    }


}