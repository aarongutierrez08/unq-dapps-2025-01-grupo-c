package com.example.unq_dapps_2025_01_grupo_c.service

import Match
import com.example.unq_dapps_2025_01_grupo_c.exceptions.TeamNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.service.external.FootballDataApiClient
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class FootballDataService(
    private val footballDataApiClient: FootballDataApiClient
) {
    private val premierLeagueId = 2021

    @Cacheable("premierLeagueTeams")
    fun getPremierLeagueTeams(): Map<String, Int> {
        return footballDataApiClient.getCompetitionTeams(premierLeagueId)
    }

    @Cacheable("upcomingMatches", key = "#teamName")
    fun getUpcomingMatchesByTeamName(teamName: String): List<Match> {
        val teams = getPremierLeagueTeams()
        val teamId = teams[teamName.lowercase()]
            ?: throw TeamNotFoundException(teamName)

        return footballDataApiClient.getTeamUpcomingMatches(teamId)
    }
}