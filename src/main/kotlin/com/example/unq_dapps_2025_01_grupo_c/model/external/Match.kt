package com.example.unq_dapps_2025_01_grupo_c.model.external

import java.time.ZonedDateTime

data class Match(
    val id: Int,
    val utcDate: ZonedDateTime,
    val status: String,
    val matchday: Int?,
    val stage: String?,
    val group: String?,
    val lastUpdated: ZonedDateTime?,
    val homeTeam: Team,
    val awayTeam: Team,
    val competition: Competition,
    val area: Area,
    val season: Season,
    val score: Score,
    val odds: Odds?,
    val referees: List<Referee> = emptyList()
)

data class MatchApiResponse(
    val matches: List<Match>
)

data class TeamMatchesResponse(
    val matches: List<Match>
)

enum class TeamSide {
    HOME_TEAM,
    AWAY_TEAM,
    DRAW
}

enum class Duration {
    REGULAR,
    EXTRA_TIME,
    PENALTY_SHOOTOUT
}

data class Score(
    val winner: TeamSide?,
    val duration: Duration,
    val fullTime: FullTimeScore,
    val halfTime: FullTimeScore?,
    val regularTime: FullTimeScore?,
    val extraTime: FullTimeScore?,
    val penalties: FullTimeScore?
)

data class FullTimeScore(
    val home: Int?,
    val away: Int?
)