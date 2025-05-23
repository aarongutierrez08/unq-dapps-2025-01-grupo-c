package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.exceptions.TeamNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@Tag("scrapping")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class WhoScoredServiceTest {

    private val whoScoredScraperService = WhoScoredService()

    @Test
    fun `fetchPlayers should return a list of players when team is found`() {
        val teamName = "Liverpool"
        val players = whoScoredScraperService.fetchPlayers(teamName)

        assertTrue(players.contains("Mohamed Salah"))
    }

    @Test
    fun `fetchPlayers should return TeamNotFoundException when team is not found`() {
        val teamName = "Boca Juniors"

        assertThrows<TeamNotFoundException> {
            whoScoredScraperService.fetchPlayers(teamName)
        }
    }
}