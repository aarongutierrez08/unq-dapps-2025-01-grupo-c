package com.example.unq_dapps_2025_01_grupo_c.repository

import com.example.unq_dapps_2025_01_grupo_c.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PlayerRepository : JpaRepository<Player, Long> {
    fun findByname(username: String): Optional<Player>

}