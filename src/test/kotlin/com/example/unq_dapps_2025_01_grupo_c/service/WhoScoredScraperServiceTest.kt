package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.exceptions.TeamNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class WhoScoredScraperServiceTest {

    private val scraperService = WhoScoredScraperService()

    @Test
    fun `fetchPlayers should return a list of players when team is found`() {
        val teamName = "Boca Juniors"
        val players = scraperService.fetchPlayers(teamName)

        assertTrue(players.contains("Edinson Cavani"))
    }

    @Test
    fun `fetchPlayers should return TeamNotFoundException when team is not found`() {
        val teamName = "Manchester"

        assertThrows<TeamNotFoundException> {
            scraperService.fetchPlayers(teamName)
        }
    }
}