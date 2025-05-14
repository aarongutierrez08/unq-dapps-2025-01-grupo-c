package com.example.unq_dapps_2025_01_grupo_c

import com.example.unq_dapps_2025_01_grupo_c.model.Player
import com.example.unq_dapps_2025_01_grupo_c.model.Team
import com.example.unq_dapps_2025_01_grupo_c.repository.PlayerRepository
import com.example.unq_dapps_2025_01_grupo_c.repository.TeamRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataSeeder(
    private val jugadorRepository: PlayerRepository,
    private val equipoRepository: TeamRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println(">>> Seeding data...")
        if (jugadorRepository.count() == 0L) {
            val river = equipoRepository.save(Team(name = "River Plate"))
            val boca = equipoRepository.save(Team(name = "Boca Juniors"))

            jugadorRepository.saveAll(
                listOf(
                    Player(name = "Enzo Pérez", team = river),
                    Player(name = "Darío Benedetto", team = boca)
                )
            )

            println("Datos de prueba insertados.")
        }
    }
}