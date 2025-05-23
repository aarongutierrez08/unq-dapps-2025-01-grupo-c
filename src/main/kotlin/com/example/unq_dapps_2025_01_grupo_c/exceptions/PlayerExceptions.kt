package com.example.unq_dapps_2025_01_grupo_c.exceptions

class PlayerNotFoundException(playerName: String) : RuntimeException("Player $playerName not found")