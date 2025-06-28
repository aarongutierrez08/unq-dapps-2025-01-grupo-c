package com.example.unq_dapps_2025_01_grupo_c.service.external

import com.example.unq_dapps_2025_01_grupo_c.external.FootballDataApiClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration

import com.example.unq_dapps_2025_01_grupo_c.model.external.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.context.ActiveProfiles
import java.time.ZonedDateTime

fun generateValidMatchApiResponseJson(objectMapper: ObjectMapper): String {
    val response = MatchApiResponse(
        matches = listOf(
            Match(
                id = 1,
                utcDate = ZonedDateTime.now(),
                status = "SCHEDULED",
                matchday = 38,
                stage = "REGULAR_SEASON",
                group = null,
                lastUpdated = ZonedDateTime.now(),
                homeTeam = Team(
                    id = 1,
                    name = "Arsenal FC",
                    shortName = "Arsenal",
                    tla = "ARS",
                    crest = "https://example.com/crest.png"
                ),
                awayTeam = Team(
                    id = 2,
                    name = "Liverpool FC",
                    shortName = "Liverpool",
                    tla = "LIV",
                    crest = "https://example.com/crest2.png"
                ),
                competition = Competition(
                    id = 2021,
                    name = "Premier League",
                    code = "PL",
                    type = "LEAGUE",
                    emblem = "https://example.com/emblem.png",
                    area = Area(
                        id = 2072,
                        name = "England",
                        code = "ENG",
                        flag = "https://example.com/eng.png"
                    )
                ),
                area = Area(
                    id = 2072,
                    name = "England",
                    code = "ENG",
                    flag = "https://example.com/eng.png"
                ),
                season = Season(
                    id = 135,
                    startDate = "2024-08-01",
                    endDate = "2025-05-31",
                    currentMatchday = 38
                ),
                score = Score(
                    fullTime = FullTimeScore(null, null),
                    halfTime = FullTimeScore(null, null),
                    winner = null,
                    duration = Duration.REGULAR,
                    regularTime = null,
                    extraTime = null,
                    penalties = null,
                ),
                odds = Odds(
                    msg = null,
                    homeWin = null,
                    draw = null,
                    awayWin = null
                ),
                referees = listOf(
                    Referee(id = 1, name = "Mike Dean", type = "REF", nationality = "English")
                )
            )
        )
    )

    return objectMapper.writeValueAsString(response)
}

fun generateValidTeamsApiResponseJson(objectMapper: ObjectMapper): String {
    val response = TeamsApiResponse(
        teams = listOf(
            Team(
                id = 1,
                name = "Arsenal FC",
                shortName = "Arsenal",
                tla = "ARS",
                crest = "https://example.com/crest.png"
            )
        )
    )
    return objectMapper.writeValueAsString(response)
}

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(initializers = [FootballDataApiClientTest.Companion.Initializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FootballDataApiClientTest {

    @Autowired
    lateinit var footballDataApiClient: FootballDataApiClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        val mockWebServer = MockWebServer()

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(config: ConfigurableApplicationContext) {
                mockWebServer.start() // ✅ SOLO SE LLAMA UNA VEZ ACÁ
                TestPropertyValues.of(
                    "football-data.api.base-url=${mockWebServer.url("/")}",
                    "football-data.api-key=test-key"
                ).applyTo(config.environment)
            }
        }
    }

    @AfterAll
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should return upcoming matches`() {
        val mockResponseJson = generateValidMatchApiResponseJson(objectMapper)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json")
        )


        val result = footballDataApiClient.getTeamUpcomingMatches(1)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(1, result?.size)
        Assertions.assertEquals("SCHEDULED", result?.first()?.status)
    }

    @Test
    fun `should return competition teams`() {
        val mockResponseJson = generateValidTeamsApiResponseJson(objectMapper)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json")
        )

        val result = footballDataApiClient.getCompetitionTeams(2021)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(1, result?.size)
        Assertions.assertEquals("Arsenal FC", result?.first()?.name)
    }

    @Test
    fun `should return last matches for 2024 and 2023`() {
        val mockResponseJson = generateValidMatchApiResponseJson(objectMapper)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json")
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json")
        )

        val result = footballDataApiClient.getCompetitionLastMatches(2021)

        Assertions.assertEquals(2, result.size)
        Assertions.assertEquals("SCHEDULED", result[0].status)
    }

}
