package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.dto.auth.AuthRequest
import com.example.unq_dapps_2025_01_grupo_c.exceptions.InvalidCredentialsException
import com.example.unq_dapps_2025_01_grupo_c.exceptions.UserAlreadyExistsException
import com.example.unq_dapps_2025_01_grupo_c.model.user.User
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import com.example.unq_dapps_2025_01_grupo_c.security.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    private val encoder = BCryptPasswordEncoder()

    fun register(request: AuthRequest): String {
        if (userRepository.findByUsername(request.username).isPresent) {
            throw UserAlreadyExistsException("User '${request.username}' already exists.")
        }

        val hashedPassword = encoder.encode(request.password)
        val user = User(username = request.username, password = hashedPassword)
        userRepository.save(user)

        return jwtUtil.generateToken(user.username)
    }

    fun login(request: AuthRequest): String {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow {
                InvalidCredentialsException()
            }

        if (!encoder.matches(request.password, user.password)) {
            throw InvalidCredentialsException()
        }

        return jwtUtil.generateToken(user.username)
    }
}
