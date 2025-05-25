package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.match.MatchPredictionRequest
import com.example.unq_dapps_2025_01_grupo_c.dto.match.UpcomingMatchesRequest
import com.example.unq_dapps_2025_01_grupo_c.model.user.User
import com.example.unq_dapps_2025_01_grupo_c.repository.QueryHistoryRepository
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import com.example.unq_dapps_2025_01_grupo_c.security.JwtUtil
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

@ActiveProfiles("prod")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var queryHistoryRepository: QueryHistoryRepository

    private val encoder = BCryptPasswordEncoder()
    private lateinit var token: String
    private var username = "arito3"
    private var password = "arito123"

    @BeforeEach
    fun setup() {
        if (!userRepository.existsByUsername(username)) {
            val user = User(username = username, password = encoder.encode(password))
            userRepository.save(user)
        }
        token = jwtUtil.generateToken(username)
    }

    @AfterEach
    fun tearDown() {
        queryHistoryRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `should return upcoming matches of the team by teamName`() {
        val request = UpcomingMatchesRequest(teamName = "arsenal")

        mockMvc.post("/match/upcoming-matches") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$", hasSize<Any>(0)) //zero because no more matches in premiere league 2025
        }
    }

    @Test
    fun `should return 404 when teamName not found`() {
        val request = UpcomingMatchesRequest(teamName = "boquita")

        mockMvc.post("/match/upcoming-matches") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", equalTo("Team ${request.teamName} not found"))
        }
    }

    @Test
    fun `should return prediction match info when teams are valid`() {
        val request = MatchPredictionRequest(teamNameOne = "arsenal", teamNameTwo = "liverpool")

        mockMvc.post("/match/prediction") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.historicalResults", hasSize<Any>(greaterThan(0)))
        }
    }

    @Test
    fun `should return 404 when no matches found`() {
        val request = MatchPredictionRequest(teamNameOne = "boquita", teamNameTwo = "riber")

        mockMvc.post("/match/prediction") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", equalTo("No matches between ${request.teamNameOne} and ${request.teamNameTwo}"))
        }
    }

}