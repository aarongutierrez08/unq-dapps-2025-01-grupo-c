package com.example.unq_dapps_2025_01_grupo_c.component

import com.example.unq_dapps_2025_01_grupo_c.dto.match.ExpectedScore
import com.example.unq_dapps_2025_01_grupo_c.dto.match.HistoricalResult
import com.example.unq_dapps_2025_01_grupo_c.dto.match.MatchPredictionResponse
import com.example.unq_dapps_2025_01_grupo_c.model.external.*
import org.springframework.stereotype.Component
import kotlin.math.*

@Component
class MatchPredictor {

    fun predictMatch(previousMatches: List<Match>, teamNameOne: String, teamNameTwo: String): MatchPredictionResponse {
        if (previousMatches.isEmpty()) {
            return MatchPredictionResponse(
                prediction = "No historical matches between these teams",
                confidence = 0.0,
                expectedScore = null,
                historicalResults = emptyList()
            )
        }

        val team1Wins = previousMatches.count {
            (matchesTeam(it.homeTeam, teamNameOne) && it.score.winner == TeamSide.HOME_TEAM) ||
                    (matchesTeam(it.awayTeam, teamNameOne) && it.score.winner == TeamSide.AWAY_TEAM)
        }

        val team2Wins = previousMatches.count {
            (matchesTeam(it.homeTeam, teamNameTwo) && it.score.winner == TeamSide.HOME_TEAM) ||
                    (matchesTeam(it.awayTeam, teamNameTwo) && it.score.winner == TeamSide.AWAY_TEAM)
        }

        val draws = previousMatches.count { it.score.winner == TeamSide.DRAW }

        val team1Goals = previousMatches.map { match ->
            if (matchesTeam(match.homeTeam, teamNameOne)) match.score.fullTime.home ?: 0 else match.score.fullTime.away ?: 0
        }.average()

        val team2Goals = previousMatches.map { match ->
            if (matchesTeam(match.homeTeam, teamNameTwo)) match.score.fullTime.home ?: 0 else match.score.fullTime.away ?: 0
        }.average()

        val (prediction, confidence) = when {
            team1Wins > team2Wins && team1Wins > draws -> Pair("$teamNameOne Victory", team1Wins.toDouble() / previousMatches.size)
            team2Wins > team1Wins && team2Wins > draws -> Pair("$teamNameTwo Victory", team2Wins.toDouble() / previousMatches.size)
            else -> Pair("Draw", draws.toDouble() / previousMatches.size)
        }

        return MatchPredictionResponse(
            prediction = prediction,
            confidence = (confidence * 100).roundToInt().toDouble(),
            expectedScore = ExpectedScore(
                teamOne = team1Goals.roundToInt(),
                teamTwo = team2Goals.roundToInt()
            ),
            historicalResults = previousMatches.takeLast(5).map {
                HistoricalResult(
                    date = it.utcDate,
                    teamOne = it.homeTeam.name,
                    teamTwo = it.awayTeam.name,
                    score = "${it.score.fullTime.home}-${it.score.fullTime.away}",
                    winner = when(it.score.winner) {
                        TeamSide.HOME_TEAM -> it.homeTeam.name
                        TeamSide.AWAY_TEAM -> it.awayTeam.name
                        else -> "Draw"
                    }
                )
            }
        )
    }

    private fun matchesTeam(team: Team, identifier: String): Boolean {
        return team.name.equals(identifier, ignoreCase = true) ||
                team.shortName.equals(identifier, ignoreCase = true) ||
                team.tla.equals(identifier, ignoreCase = true)
    }

}