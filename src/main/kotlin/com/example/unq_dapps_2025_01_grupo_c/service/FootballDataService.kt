package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.model.external.team.Match
import com.example.unq_dapps_2025_01_grupo_c.model.external.team.Team
import com.example.unq_dapps_2025_01_grupo_c.exceptions.PlayerNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.exceptions.TeamNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.service.external.FootballDataApiClient
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class FootballDataService(
    private val footballDataApiClient: FootballDataApiClient
) {
    private val premierLeagueId = 2021

    @Cacheable("upcomingMatches", key = "#teamName")
    fun getUpcomingMatchesByTeamName(teamName: String): List<Match> {
        val teams = footballDataApiClient.getCompetitionTeams(premierLeagueId)
        val team = teams?.find { it.name.equals(teamName, ignoreCase = true) || it.shortName.equals(teamName, ignoreCase = true) }
            ?: throw TeamNotFoundException(teamName)

        return footballDataApiClient.getTeamUpcomingMatches(team.id)
    }

    @Cacheable("teamPlayer", key = "#playerName")
    fun getTeamByNamePlayer(playerName: String): Team {
        val teams = footballDataApiClient.getCompetitionTeams(premierLeagueId)
        val playerTeam: Team = teams?.find { team ->
            team.squad?.any { player -> player.name.equals(playerName, ignoreCase = true) } ?: false
        } ?: throw PlayerNotFoundException(playerName)
        return playerTeam
    }
}