package com.example.unq_dapps_2025_01_grupo_c.bootstrap

import com.example.unq_dapps_2025_01_grupo_c.model.User
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("dev", "test")
@Component
class DataInitializer(
    private val userRepository: UserRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createUserIfNotExists("admin", "admin123")
        createUserIfNotExists("arito", "arito123")
    }

    private fun createUserIfNotExists(username: String, password: String) {
        val exists = userRepository.findByUsername(username).isPresent
        if (!exists) {
            userRepository.save(User(username = username, password = password))
        }
    }

}
