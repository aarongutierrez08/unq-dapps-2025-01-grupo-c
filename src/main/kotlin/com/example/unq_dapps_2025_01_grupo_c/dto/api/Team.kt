data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
)

data class TeamsApiResponse(
    val teams: List<Team>
)

data class Match(
    val id: Int,
    val utcDate: String,
    val status: String,
    val matchday: Int?,
    val homeTeam: Team,
    val awayTeam: Team,
    val competition: CompetitionInfo
)

data class TeamMatchesResponse(
    val matches: List<Match>
)

data class CompetitionInfo(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String?
)