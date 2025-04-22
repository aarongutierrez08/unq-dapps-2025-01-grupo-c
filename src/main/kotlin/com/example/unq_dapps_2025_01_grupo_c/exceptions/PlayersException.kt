package com.example.unq_dapps_2025_01_grupo_c.exceptions

class PlayersNotFoundException(message: String) : RuntimeException(message)
class TeamNotFoundException(teamName: String) : RuntimeException("Team $teamName not found")