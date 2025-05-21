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
class WhoScoredService {

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

        driver.get("$baseUrl/regions/252/tournaments/2/inglaterra-premier-league")
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

        if (teamPlayers.isEmpty())

        driver.quit()

        return teamPlayers
    }
}