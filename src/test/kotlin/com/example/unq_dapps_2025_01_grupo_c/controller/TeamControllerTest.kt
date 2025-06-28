package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.player.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.security.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        token = jwtUtil.generateToken("arito")
    }

    @Test
    fun `should return players when authorized`() {
        val request = PlayerRequest(team = "Liverpool")

        mockMvc.post("/team/players") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$", hasSize<Any>(greaterThan(0)))
        }
    }

    @Test
    fun `should return 404 when team is invalid`() {
        val request = PlayerRequest(team = "InvalidTeam")

        mockMvc.post("/team/players") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message", equalTo("Team ${request.team} not found"))
        }
    }

    @Test
    fun `should return 401 when token is missing`() {
        val request = PlayerRequest(team = "Liverpool")

        mockMvc.post("/team/players") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isUnauthorized() }
        }
    }
}