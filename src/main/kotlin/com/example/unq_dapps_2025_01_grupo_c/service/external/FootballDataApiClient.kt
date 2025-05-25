package com.example.unq_dapps_2025_01_grupo_c.service.external

import com.example.unq_dapps_2025_01_grupo_c.model.external.*
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

    private val webClient by lazy {
        webClientBuilder.baseUrl(baseUrl)
            .defaultHeader("X-Auth-Token", apiKey)
            .codecs { it.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) }
            .build()
    }

    fun getTeamUpcomingMatches(teamId: Int): List<Match>? {
        return webClient.get()
            .uri("/teams/$teamId/matches?status=SCHEDULED")
            .retrieve()
            .bodyToMono<TeamMatchesResponse>()
            .block()
            ?.matches
    }

    fun getCompetitionTeams(competitionId: Int): List<Team>? {
        return webClient.get()
            .uri("/competitions/$competitionId/teams")
            .retrieve()
            .bodyToMono<TeamsApiResponse>()
            .block()
            ?.teams
    }

    fun getCompetitionLastMatches(competitionId: Int): List<Match> {
        val season2024Matches: List<Match> = webClient.get()
            .uri("/competitions/$competitionId/matches?status=FINISHED&season=2024")
            .retrieve()
            .bodyToMono<MatchApiResponse>()
            .block()
            ?.matches ?: emptyList()

        val season2023Matches: List<Match> = webClient.get()
            .uri("/competitions/$competitionId/matches?status=FINISHED&season=2023")
            .retrieve()
            .bodyToMono<MatchApiResponse>()
            .block()
            ?.matches ?: emptyList()

        return season2024Matches + season2023Matches
    }
}