package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.dto.match.MatchPredictionResponse
import com.example.unq_dapps_2025_01_grupo_c.exceptions.NoMatchBetweenException
import com.example.unq_dapps_2025_01_grupo_c.model.external.Match
import com.example.unq_dapps_2025_01_grupo_c.model.external.Team
import com.example.unq_dapps_2025_01_grupo_c.exceptions.PlayerNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.exceptions.TeamNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.service.external.FootballDataApiClient
import com.example.unq_dapps_2025_01_grupo_c.service.predictor.MatchPredictor
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class FootballDataService(
    private val footballDataApiClient: FootballDataApiClient,
    private val matchPredictor: MatchPredictor
) {
    private val premierLeagueId = 2021

    @Cacheable("upcomingMatches", key = "#teamName")
    fun getUpcomingMatchesByTeamName(teamName: String): List<Match>? {
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

    @Cacheable("matchPrediction", key = "#teamNameOne + '_' + #teamNameTwo")
    fun getMatchPredictionTo(teamNameOne: String, teamNameTwo: String): MatchPredictionResponse {
        val matches = footballDataApiClient.getCompetitionLastMatches(premierLeagueId)
        val matchesBetweenTeams = findMatchesBetweenTeams(matches, teamNameOne, teamNameTwo)
        if (matchesBetweenTeams.isEmpty()) {
            throw NoMatchBetweenException(teamNameOne, teamNameTwo)
        }
        val prediction = matchPredictor.predictMatch(matchesBetweenTeams, teamNameOne, teamNameTwo)
        return prediction
    }

    private fun findMatchesBetweenTeams(
        allMatches: List<Match>,
        team1Identifier: String,
        team2Identifier: String,
        ignoreCase: Boolean = true
    ): List<Match> {
        return allMatches.filter { match ->
            val home = match.homeTeam
            val away = match.awayTeam

            (matchesAnyIdentifier(home, team1Identifier, ignoreCase) &&
                    matchesAnyIdentifier(away, team2Identifier, ignoreCase)) ||
                    (matchesAnyIdentifier(home, team2Identifier, ignoreCase) &&
                            matchesAnyIdentifier(away, team1Identifier, ignoreCase))
        }
    }

    private fun matchesAnyIdentifier(team: Team, identifier: String, ignoreCase: Boolean): Boolean {
        return team.name.equals(identifier, ignoreCase) ||
                team.shortName.equals(identifier, ignoreCase) ||
                team.tla.equals(identifier, ignoreCase)
    }
}