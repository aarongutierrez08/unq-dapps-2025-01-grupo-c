package com.example.unq_dapps_2025_01_grupo_c.service.predictor

import com.example.unq_dapps_2025_01_grupo_c.component.MatchPredictor
import com.example.unq_dapps_2025_01_grupo_c.dto.match.MatchPredictionResponse
import com.example.unq_dapps_2025_01_grupo_c.model.external.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.ZonedDateTime

@ActiveProfiles("test")
@SpringBootTest
class MatchPredictorTest {

    private val predictor = MatchPredictor()

    private fun createMatch(
        homeTeamName: String,
        awayTeamName: String,
        homeScore: Int?,
        awayScore: Int?,
        winner: TeamSide
    ): Match {
        return Match(
            id = 1,
            utcDate = ZonedDateTime.now(),
            status = "FINISHED",
            matchday = null,
            stage = null,
            group = null,
            lastUpdated = ZonedDateTime.now(),
            homeTeam = Team(1, homeTeamName, homeTeamName, homeTeamName),
            awayTeam = Team(2, awayTeamName, awayTeamName, awayTeamName),
            competition = Competition(0, "", "", "", null),
            area = Area(0, "", "", null),
            season = Season(0, "", "", null),
            score = Score(
                fullTime = FullTimeScore(homeScore, awayScore),
                halfTime = FullTimeScore(null, null),
                winner = winner,
                duration = Duration.REGULAR,
                regularTime = null,
                extraTime = null,
                penalties = null
            ),
            odds = null,
            referees = emptyList()
        )
    }

    @Test
    fun `should return draw prediction`() {
        val matches = listOf(
            createMatch("TeamA", "TeamB", 1, 1, TeamSide.DRAW),
            createMatch("TeamA", "TeamB", 0, 0, TeamSide.DRAW),
            createMatch("TeamA", "TeamB", 2, 2, TeamSide.DRAW),
        )

        val response: MatchPredictionResponse = predictor.predictMatch(matches, "TeamA", "TeamB")
        assertEquals("Draw", response.prediction)
        assertEquals(100.0, response.confidence)
        assertNotNull(response.expectedScore)
        assertEquals(1, response.expectedScore?.teamOne)
        assertEquals(1, response.expectedScore?.teamTwo)
    }

    @Test
    fun `should return TeamA victory`() {
        val matches = listOf(
            createMatch("TeamA", "TeamB", 2, 0, TeamSide.HOME_TEAM),
            createMatch("TeamB", "TeamA", 0, 1, TeamSide.AWAY_TEAM),
        )

        val response = predictor.predictMatch(matches, "TeamA", "TeamB")
        assertEquals("TeamA Victory", response.prediction)
        assertEquals(100.0, response.confidence)
    }

    @Test
    fun `should return TeamB victory`() {
        val matches = listOf(
            createMatch("TeamB", "TeamA", 2, 0, TeamSide.HOME_TEAM),
            createMatch("TeamA", "TeamB", 0, 1, TeamSide.AWAY_TEAM),
        )

        val response = predictor.predictMatch(matches, "TeamA", "TeamB")
        assertEquals("TeamB Victory", response.prediction)
        assertEquals(100.0, response.confidence)
    }

    @Test
    fun `should return no historical matches`() {
        val response = predictor.predictMatch(emptyList(), "TeamA", "TeamB")
        assertEquals("No historical matches between these teams", response.prediction)
        assertEquals(0.0, response.confidence)
        assertNull(response.expectedScore)
    }
}
