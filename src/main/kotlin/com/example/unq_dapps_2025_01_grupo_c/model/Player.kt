package com.example.unq_dapps_2025_01_grupo_c.model

import jakarta.persistence.*

@Entity
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O AUTO si preferís
    var id: Long? = null,

    val name: String = "",

    @ManyToOne
    val team: Team = Team()
) {
    constructor() : this(null, "", Team())
}