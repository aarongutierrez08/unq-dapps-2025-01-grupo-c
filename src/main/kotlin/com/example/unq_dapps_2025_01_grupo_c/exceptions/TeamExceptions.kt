package com.example.unq_dapps_2025_01_grupo_c.exceptions

class TeamNotFoundException(teamName: String) : RuntimeException("Team $teamName not found")