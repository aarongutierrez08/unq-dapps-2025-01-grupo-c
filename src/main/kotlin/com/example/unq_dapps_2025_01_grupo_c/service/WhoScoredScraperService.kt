package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.dto.MatchDTO
import com.example.unq_dapps_2025_01_grupo_c.exceptions.MatchesNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.exceptions.PlayersNotFoundException
import com.example.unq_dapps_2025_01_grupo_c.exceptions.TeamNotFoundException
import org.openqa.selenium.By
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.NoSuchElementException

@Service
class WhoScoredScraperService {

    fun createDriver(): WebDriver {
        val options = ChromeOptions().apply {
            addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
            )
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
//            setBinary(System.getenv("CHROME_BIN") ?: "/usr/bin/google-chrome")
        }
        return ChromeDriver(options)
    }

    fun fetchPlayers(teamName: String): List<String> {
        val driver: WebDriver = createDriver()
        val baseUrl = "https://es.whoscored.com"

        driver.get("$baseUrl/regions/11/tournaments/68/seasons/10573/argentina-liga-profesional")
        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("standings")))
        val teamsTable = driver.findElement(By.className("standings"))
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-link")))
        val links = teamsTable.findElements(By.className("team-link"))

        val targetLink = links.firstOrNull { it.text.equals(teamName, true) || it.text.contains(teamName, true) }
            ?.getAttribute("href") ?: throw TeamNotFoundException(teamName)

        driver.get(targetLink)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")))
        val playersTable = driver.findElement(By.id("player-table-statistics-body"))

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("iconize")))

        val teamPlayers = playersTable.findElements(By.className("iconize"))
            .mapNotNull { it.text.takeIf { txt -> txt.isNotBlank() } }

        if (teamPlayers.isEmpty()) throw PlayersNotFoundException("Players of $teamName not found")

        driver.quit()

        return teamPlayers
    }

    fun fetchNextMatches(teamName: String): List<MatchDTO> {
        val driver: WebDriver = createDriver()
        val baseUrl = "https://es.whoscored.com"
        val wait = WebDriverWait(driver, Duration.ofSeconds(120)) // Aumentado por si acaso

        try {
            driver.get("$baseUrl/regions/11/tournaments/68/seasons/10573/argentina-liga-profesional") // Ajusta si es necesario
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("standings")))
            val teamsTable = driver.findElement(By.className("standings"))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-link")))
            val links = teamsTable.findElements(By.className("team-link"))

            val teamPageLink = links.firstOrNull { it.text.equals(teamName, true) || it.text.contains(teamName, true) }
                ?.getAttribute("href") ?: throw TeamNotFoundException(teamName)

            driver.get(teamPageLink)

            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("fixtures"))) // Ajusta "Partidos" si es diferente
            val fixturesLink = driver.findElement(By.linkText("Overall"))
            fixturesLink.click()

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("team-fixture-wrapper"))) // Ajusta 'team-fixtures' si es diferente
            val fixturesTableBody = driver.findElement(By.cssSelector("#team-fixtures tbody")) // Busca el tbody dentro de la tabla

            val matchRows = fixturesTableBody.findElements(By.tagName("tr"))

            val upcomingMatches = mutableListOf<MatchDTO>()
            val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm", Locale("es", "ES")) // Ajusta el patrón y Locale según WhoScored

            for (row in matchRows) {
                try {
                    // Intentar extraer datos de la fila. Los selectores pueden variar.
                    val columns = row.findElements(By.tagName("td"))

                    // Verificar si la fila representa un partido futuro.
                    // WhoScored podría tener una clase específica, un estado, o simplemente estar en orden cronológico.
                    // Aquí asumimos que los partidos sin resultado son futuros.
                    // El índice de la columna de resultado puede variar.
                    val resultColumnIndex = 5 // AJUSTA ESTE ÍNDICE SEGÚN LA ESTRUCTURA REAL!
                    val resultText = columns.getOrNull(resultColumnIndex)?.text?.trim() ?: ""

                    // Si el resultado está vacío o indica un horario (ej: "18:00"), asumimos que es futuro.
                    // Esta lógica puede necesitar refinamiento basado en cómo WhoScored marca los partidos futuros.
                    if (resultText.isBlank() || resultText.contains(":")) {

                        // Extraer datos (los índices de las columnas son ejemplos, AJÚSTALOS):
                        val dateStr = columns.getOrNull(1)?.text?.trim() ?: "" // Índice 1 para fecha/hora
                        val competition = columns.getOrNull(0)?.text?.trim() ?: "N/A" // Índice 0 para competición
                        val homeTeam = columns.getOrNull(3)?.text?.trim() ?: "N/A" // Índice 3 para equipo local
                        val awayTeam = columns.getOrNull(6)?.text?.trim() ?: "N/A" // Índice 6 para equipo visitante

                        // Combinar fecha y hora si vienen separadas o parsear directamente
                        // WhoScored a veces pone la hora en la columna de resultado para partidos futuros.
                        val dateTimeStr = if (resultText.contains(":")) "$dateStr $resultText" else dateStr
                        var matchDate: LocalDateTime? = null
                        try {
                            // Intentar parsear la fecha/hora. Puede fallar si el formato es inesperado.
                            // Necesitarás ajustar el patrón del formatter.
                            // Ejemplo: "sáb, 15 jun 2024 18:00" -> "EEE, dd MMM yyyy HH:mm"
                            matchDate = LocalDateTime.parse(dateTimeStr, formatter)
                        } catch (e: DateTimeParseException) {
                            println("Error parseando fecha: '$dateTimeStr' - ${e.message}")
                            // Podrías intentar con otros formatos o dejarla como null/String
                        }


                        if (matchDate != null && matchDate.isAfter(LocalDateTime.now())) { // Asegurarse de que es realmente futuro
                            upcomingMatches.add(
                                MatchDTO(
                                    homeTeam = homeTeam,
                                    awayTeam = awayTeam,
                                    date = matchDate, // O usa dateTimeStr si prefieres String
                                    competition = competition
                                )
                            )
                        }


                        // Salir si ya tenemos 5 partidos
                        if (upcomingMatches.size >= 5) {
                            break
                        }
                    }
                } catch (e: Exception) {
                    // Ignorar filas que no se puedan parsear (pueden ser cabeceras, separadores, etc.)
                    println("Error procesando fila de partido: ${e.message}")
                }
            }

            if (upcomingMatches.isEmpty()) {
                throw MatchesNotFoundException("No upcoming matches found for $teamName")
            }

            return upcomingMatches

        } catch (e: NoSuchElementException) {
            println("Error finding element: ${e.message}")
            // Podrías lanzar excepciones más específicas si sabes qué elemento faltó
            throw RuntimeException("Failed to scrape match data for $teamName. Element not found.", e)
        } catch (e: Exception) { // Captura genérica para otros errores (timeouts, etc.)
            println("Error during scraping for $teamName: ${e.message}")
            throw RuntimeException("An error occurred while fetching matches for $teamName", e)
        } finally {
            driver.quit() // Asegurarse de cerrar el driver siempre
        }
    }
}
