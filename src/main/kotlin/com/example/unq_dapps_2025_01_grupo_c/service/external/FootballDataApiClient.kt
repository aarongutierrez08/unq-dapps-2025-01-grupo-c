package com.example.unq_dapps_2025_01_grupo_c.service.external

import com.example.unq_dapps_2025_01_grupo_c.model.external.team.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class FootballDataApiClient(
    private val webClientBuilder: WebClient.Builder
) {
    @Value("\${football-data.api.base-url}")
    private lateinit var baseUrl: String

    @Value("\${football-data.api-key}")
    private lateinit var apiKey: String

    fun getTeamUpcomingMatches(teamId: Int): List<Match> {
        val webClient = webClientBuilder.baseUrl(baseUrl).build()

        return webClient.get()
            .uri("/teams/$teamId/matches?status=SCHEDULED")
            .header("X-Auth-Token", apiKey)
            .retrieve()
            .bodyToMono<TeamMatchesResponse>()
            .block()
            ?.matches?.map { it.toDomainModel() }
            ?: emptyList()
    }

    fun getCompetitionTeams(competitionId: Int): List<Team>? {
        val webClient = webClientBuilder.baseUrl(baseUrl).build()

        return webClient.get()
            .uri("/competitions/$competitionId/teams")
            .header("X-Auth-Token", apiKey)
            .retrieve()
            .bodyToMono<TeamsApiResponse>()
            .block()
            ?.teams
    }
}

private fun Match.toDomainModel(): Match {
    return Match(
        id = this.id,
        utcDate = this.utcDate,
        status = this.status,
        matchDay = this.matchDay,
        homeTeam = Team(
            id = this.homeTeam.id,
            name = this.homeTeam.name,
            shortName = this.homeTeam.shortName,
            tla = this.homeTeam.tla,
        ),
        awayTeam = Team(
            id = this.awayTeam.id,
            name = this.awayTeam.name,
            shortName = this.awayTeam.shortName,
            tla = this.awayTeam.tla
        ),
        competition = Competition(
            id = this.competition.id,
            name = this.competition.name,
            code = this.competition.code,
            type = this.competition.type,
            emblem = this.competition.emblem
        )
    )
}