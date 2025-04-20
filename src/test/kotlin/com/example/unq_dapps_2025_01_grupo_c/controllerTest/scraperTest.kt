//package com.example.unq_dapps_2025_01_grupo_c.controllerTest
//
//import com.example.unq_dapps_2025_01_grupo_c.controller.WhoScoredScraperController
//import com.example.unq_dapps_2025_01_grupo_c.security.JwtUtil
//import com.example.unq_dapps_2025_01_grupo_c.service.WhoScoredScraperService
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.MediaType
//import org.springframework.test.context.junit.jupiter.SpringExtension
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//
//@ExtendWith(SpringExtension::class)
//@WebMvcTest(WhoScoredScraperController::class)
//
//class WhoScoredScraperControllerTest {
//
//    @Autowired
//    private lateinit var mockMvc: MockMvc
//
//    @Mock
//    private lateinit var scraperService: WhoScoredScraperService
//
//    @Mock
//    private lateinit var jwtUtil: JwtUtil
//
//    @Configuration
//    class TestConfig {
//        @Bean
//        fun scraperService(mock: WhoScoredScraperService): WhoScoredScraperService = mock
//
//        @Bean
//        fun jwtUtil(mock: JwtUtil): JwtUtil = mock
//    }
//
//    @Test
//    fun `getPlayers should return a list of players`() {
//        val teamName = "River Plate"
//        val players = listOf("Jugador 1", "Jugador 2")
//
//        `when`(scraperService.fetchPlayers(teamName)).thenReturn(players)
//
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/players")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("""{"team": "$teamName"}""")
//        )
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(players.size))
//    }
//
//    @Test
//    fun `getPlayers should return an error message when team is not found`() {
//        val teamName = "Equipo Inexistente"
//        val errorMessage = listOf("ERROR: Team not found")
//
//        `when`(scraperService.fetchPlayers(teamName)).thenReturn(errorMessage)
//
//        mockMvc.perform(
//            MockMvcRequestBuilders.post("/players")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("""{"team": "$teamName"}""")
//        )
//            .andExpect(MockMvcResultMatchers.status().isOk)
//            .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(errorMessage.first()))
//    }
//}