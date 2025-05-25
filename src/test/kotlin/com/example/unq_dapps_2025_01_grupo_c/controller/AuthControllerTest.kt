package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.auth.AuthRequest
import com.example.unq_dapps_2025_01_grupo_c.model.user.User
import com.example.unq_dapps_2025_01_grupo_c.repository.QueryHistoryRepository
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("prod")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var queryHistoryRepository: QueryHistoryRepository

    private val encoder = BCryptPasswordEncoder()

    private var username = "arito2"
    private var password = "arito123"

    @BeforeEach
    fun setup() {
        if (!userRepository.existsByUsername(username)) {
            val user = User(username = username, password = encoder.encode(password))
            userRepository.save(user)
        }
    }

    @AfterEach
    fun tearDown() {
        queryHistoryRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `should register user when is valid`() {
        val request = AuthRequest(username = "validUser", password = "validUser")

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            header { exists("Authorization") }
            jsonPath("$.message", equalTo("User registered"))
        }
    }

    @Test
    fun `should return 400 when user is invalid`() {
        val request = AuthRequest(username = "ar", password = "ar")

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
            header { doesNotExist("Authorization") }
        }
    }

    @Test
    fun `should sign in when user already exists`() {
        val request = AuthRequest(username = username, password = password)

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            header { exists("Authorization") }
            jsonPath("$.message", equalTo("Login successful"))
        }
    }

    @Test
    fun `should return 401 when are invalid credentials`() {
        val request = AuthRequest(username = username, password = "arito1234")

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isUnauthorized() }
            header { doesNotExist("Authorization") }
        }
    }
}