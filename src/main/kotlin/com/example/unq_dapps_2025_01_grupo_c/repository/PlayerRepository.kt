package com.example.unq_dapps_2025_01_grupo_c.repository

import com.example.unq_dapps_2025_01_grupo_c.model.Player
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository : JpaRepository<Player, Long> {
    fun findByName(name: String): Player?
    fun findByTeamId(equipoId: Long): List<Player>
}