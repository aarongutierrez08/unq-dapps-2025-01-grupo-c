package com.example.unq_dapps_2025_01_grupo_c.model

import jakarta.persistence.*

@Entity
data class Team(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O AUTO, dependiendo de tu base de datos
    var id: Long? = null,

    val name: String = ""
) {
    constructor() : this(null, "")
}
