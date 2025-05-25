package com.example.unq_dapps_2025_01_grupo_c.exceptions

class NoMatchBetweenException(teamNameOne: String, teamNameTwo: String) : RuntimeException("No matches between $teamNameOne and $teamNameTwo")
