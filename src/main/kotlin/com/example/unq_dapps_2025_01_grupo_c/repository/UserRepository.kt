package com.example.unq_dapps_2025_01_grupo_c.repository


import org.springframework.data.jpa.repository.JpaRepository
import com.example.unq_dapps_2025_01_grupo_c.model.User
import java.util.*


interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
}
