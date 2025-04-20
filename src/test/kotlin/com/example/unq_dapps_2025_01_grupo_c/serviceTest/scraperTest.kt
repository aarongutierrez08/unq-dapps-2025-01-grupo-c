package com.example.unq_dapps_2025_01_grupo_c.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class WhoScoredScraperServiceTest {

    private val scraperService = WhoScoredScraperService()

    @Test
    fun `fetchPlayers should return a list of players when team is found`() {
        // Simula un equipo válido
        val teamName = "Boca Juniors"
        val players = scraperService.fetchPlayers(teamName)

        // Verifica que la lista no esté vacía
        assertTrue(players.isNotEmpty())
    }

    @Test
    fun `fetchPlayers should return an error message when team is not found`() {
        // Simula un equipo inexistente
        val teamName = "Equipo Inexistente"
        val players = scraperService.fetchPlayers(teamName)

        // Verifica que el mensaje de error sea devuelto
        assertTrue(players.first().startsWith("ERROR"))
    }
}