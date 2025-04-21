package com.example.unq_dapps_2025_01_grupo_c.service

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

@Service
class WhoScoredScraperService {

    fun fetchPlayers(teamName: String): List<String> {
        val options = ChromeOptions().apply {
            addArguments("--headless=new")
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-gpu")
            addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
        }
        if (teamName.isBlank()) {
            return listOf("ERROR: The team name cannot be empty")
        }
        val driver: WebDriver = ChromeDriver(options)
        val baseUrl = "https://es.whoscored.com"
        println("Buscando equipo: $teamName")

        try {
            driver.get("$baseUrl/regions/11/tournaments/68/seasons/10573/argentina-liga-profesional")
            val wait = WebDriverWait(driver, Duration.ofSeconds(180))

            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("standings")))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-link")))

            val links = driver.findElements(By.className("team-link"))

            val teamInfoList = links.map {
                val text = it.text.trim()
                val href = it.getAttribute("href")
                val nameFromHref = href?.split("/")?.last()?.replace("-", " ")?.replaceFirstChar { c -> c.uppercase() } ?: ""
                //println("Link: ${it.getAttribute("outerHTML")}")
                //println("Texto: '$text' | Extraído desde href: '$nameFromHref'")
                text.ifBlank { nameFromHref } to href
            }

            val matched = teamInfoList.firstOrNull { (name, _) ->
                name.equals(teamName, ignoreCase = true) || name.contains(teamName, ignoreCase = true)
            } ?: throw TeamNotFoundException("Team $teamName not found")

            val targetLink = matched.second
            if (targetLink.isNullOrBlank()) throw RuntimeException("No se encontró un link válido para el equipo $teamName")

            driver.get(targetLink)
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")))

            val playersTable = driver.findElement(By.id("player-table-statistics-body"))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("iconize")))

            return playersTable.findElements(By.className("iconize"))
                .mapNotNull { it.text.takeIf { txt -> txt.isNotBlank() } }

        } catch (e: Exception) {
            e.printStackTrace()
            return listOf("ERROR: ${e.message}")
        } finally {
            driver.quit()
        }
    }
}
