package com.example.unq_dapps_2025_01_grupo_c.controller

import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredScraperService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean // Usar MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@ExtendWith(SpringExtension::class)
@WebMvcTest(WhoScoredScraperController::class)
class WhoScoredScraperControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc // Para simular peticiones HTTP

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var scraperService: WhoScoredScraperService

//    @MockBean
//    private lateinit var jwtUtil: JwtUtil

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getPlayers should return list of players when service returns players`() {
        val teamName = "River Plate"
        val expectedPlayers = listOf("Franco Armani", "Paulo Díaz", "Enzo Pérez")
        val requestPayload = TeamRequest(teamName)

        // Configura el mock: cuando se llame a fetchPlayers con teamName, devuelve expectedPlayers
        `when`(scraperService.fetchPlayers(teamName)).thenReturn(expectedPlayers)

        // Ejecuta la petición POST simulada
        mockMvc.perform(
            post("/players") // Usando import estático
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)) // Convierte el objeto a JSON
        )
            // Verifica las respuestas esperadas
            .andExpect(status().isOk) // Espera un 200 OK
            .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Espera contenido JSON
            .andExpect(jsonPath("$.length()").value(expectedPlayers.size)) // Verifica el tamaño de la lista
            .andExpect(jsonPath("$[0]").value(expectedPlayers[0])) // Verifica el primer elemento
            .andExpect(jsonPath("$[1]").value(expectedPlayers[1])) // Verifica el segundo elemento
            .andExpect(jsonPath("$[2]").value(expectedPlayers[2])) // Verifica el tercer elemento
    }

    @Test
    fun `getPlayers should return specific message when service indicates team not found`() {
        val teamName = "Equipo Inexistente"
        // Asumiendo que el servicio devuelve una lista con un mensaje específico en este caso
        val errorMessageList = listOf("ERROR: Team '$teamName' not found or no players listed.")
        val requestPayload = TeamRequest(teamName)

        // Configura el mock para el caso de equipo no encontrado
        `when`(scraperService.fetchPlayers(teamName)).thenReturn(errorMessageList)

        mockMvc.perform(
            post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload))
        )
            // Verifica la respuesta esperada para este caso
            // NOTA: Considera si 200 OK es apropiado aquí.
            // Quizás un 404 Not Found sería semánticamente mejor si el equipo no existe.
            // Si cambias eso en el controlador, ajusta el .andExpect(status().isNotFound())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1)) // Espera una lista con un solo elemento
            .andExpect(jsonPath("$[0]").value(errorMessageList[0])) // Verifica el mensaje de error
    }

    @Test
    fun `getPlayers should handle empty player list from service`() {
        val teamName = "Equipo Sin Jugadores Registrados"
        val emptyPlayerList = emptyList<String>()
        val requestPayload = TeamRequest(teamName)

        // Configura el mock para devolver una lista vacía
        `when`(scraperService.fetchPlayers(teamName)).thenReturn(emptyPlayerList)

        mockMvc.perform(
            post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload))
        )
            .andExpect(status().isOk) // Espera 200 OK
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0)) // Verifica que la lista JSON esté vacía
    }
}