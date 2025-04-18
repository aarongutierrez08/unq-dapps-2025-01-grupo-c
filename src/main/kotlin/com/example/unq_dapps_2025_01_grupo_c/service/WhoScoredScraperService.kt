package com.example.unq_dapps_2025_01_grupo_c.service

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
            setBinary(System.getenv("CHROME_BIN") ?: "/usr/bin/google-chrome")
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
        }

        val driver: WebDriver = ChromeDriver(options)
        val baseUrl = "https://es.whoscored.com"

        try {
            driver.get("$baseUrl/regions/11/tournaments/68/seasons/10573/argentina-liga-profesional")
            val wait = WebDriverWait(driver, Duration.ofSeconds(120))

            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("standings")))
            val teamsTable = driver.findElement(By.className("standings"))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-link")))
            val links = teamsTable.findElements(By.className("team-link"))

            val targetLink = links.firstOrNull { it.text.equals(teamName, true) || it.text.contains(teamName, true) }
                ?.getAttribute("href") ?: throw RuntimeException("Team $teamName not found")

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
