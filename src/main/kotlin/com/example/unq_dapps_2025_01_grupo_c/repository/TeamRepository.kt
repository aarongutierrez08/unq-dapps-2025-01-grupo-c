package com.example.unq_dapps_2025_01_grupo_c.repository

import com.example.unq_dapps_2025_01_grupo_c.model.Team
import org.springframework.data.jpa.repository.JpaRepository

interface TeamRepository : JpaRepository<Team, Long>{
    fun findByName(nombre: String): Team?
}