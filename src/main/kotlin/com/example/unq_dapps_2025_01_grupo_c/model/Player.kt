package com.example.unq_dapps_2025_01_grupo_c.model

import jakarta.persistence.*

@Entity
@Table(name = "players")
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,
    val age: Int,
    val position: String,
    val nationality: String,
    val appearances: Int,
    val goals: Int,
    val assists: Int,
    val rating: Double
)