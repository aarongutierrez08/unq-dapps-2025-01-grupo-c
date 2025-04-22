package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.dto.PlayerRequest
import com.example.unq_dapps_2025_01_grupo_c.exceptions.ApiErrorResponse
import com.example.unq_dapps_2025_01_grupo_c.security.JwtUtil
import com.example.unq_dapps_2025_01_grupo_c.model.User
import com.example.unq_dapps_2025_01_grupo_c.repository.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WhoScoredScraperControllerTest {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:postgresql://localhost:5432/dapps" }
            registry.add("spring.datasource.username") { "postgres" }
            registry.add("spring.datasource.password") { "postgres" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "update" }
            registry.add("spring.test.database.replace") { "none" }
        }
    }

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        if (!userRepository.findByUsername("testuser").isPresent) {
            val user = User(username = "testuser", password ="123456")
            userRepository.save(user)
        }

        token = jwtUtil.generateToken("testuser")
    }

    @Test
    fun `should return players when authorized`() {
        val request = PlayerRequest(team = "Boca Juniors")
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForEntity(
            "http://localhost:$port/players",
            entity,
            Array<String>::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `should return 404 when team is invalid`() {
        val request = PlayerRequest(team = "InvalidTeam")
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(token)
        }

        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForEntity(
            "http://localhost:$port/players",
            entity,
            ApiErrorResponse::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body!!.message == "Team ${request.team} not found")
    }

    @Test
    fun `should return 401 when token is missing`() {
        val request = PlayerRequest(team = "Boca Juniors")
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForEntity(
            "http://localhost:$port/players",
            entity,
            String::class.java
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }
}
