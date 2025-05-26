package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.model.PositionRating
import com.example.unq_dapps_2025_01_grupo_c.exceptions.PlayerNotFoundException
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

    val teamLinkClassName = "team-link"
    val standingsClassName = "standings"
    val playersTableId = "player-table-statistics-body"

    fun createDriver(): WebDriver {
        val options = ChromeOptions().apply {
            addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
                "--remote-debugging-port=9222"
            )
            setPageLoadStrategy(PageLoadStrategy.NORMAL)
            setBinary(System.getenv("CHROME_BIN") ?: "/usr/bin/google-chrome")
        }
        return ChromeDriver(options)
    }

    fun fetchPlayers(teamName: String): List<String> {
        val driver: WebDriver = createDriver()
        val baseUrl = "https://es.whoscored.com"

        driver.get("$baseUrl/regions/252/tournaments/2/inglaterra-premier-league")
        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(standingsClassName)))
        val teamsTable = driver.findElement(By.className(standingsClassName))
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(teamLinkClassName)))
        val links = teamsTable.findElements(By.className(teamLinkClassName))

        val targetLink = links.firstOrNull { it.text.equals(teamName, true) || it.text.contains(teamName, true) }
            ?.getAttribute("href") ?: throw TeamNotFoundException(teamName)

        driver.get(targetLink)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(playersTableId)))
        val playersTable = driver.findElement(By.id(playersTableId))

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("iconize")))

        val teamPlayers = playersTable.findElements(By.className("iconize"))
            .mapNotNull { it.text.takeIf { txt -> txt.isNotBlank() } }

        driver.quit()

        return teamPlayers
    }

    fun getPlayerPerformance(playerName: String, playerTeamName: String): PositionRating {
        val driver: WebDriver = createDriver()
        val baseUrl = "https://es.whoscored.com"

        driver.get("$baseUrl/regions/252/tournaments/2/inglaterra-premier-league")
        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(standingsClassName)))
        val teamsTable = driver.findElement(By.className(standingsClassName))
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(teamLinkClassName)))
        val links = teamsTable.findElements(By.className(teamLinkClassName))

        val targetLink = links.firstOrNull { playerTeamName.contains(playerTeamName, true) || it.text.equals(playerTeamName, true) }
            ?.getAttribute("href") ?: throw TeamNotFoundException(playerTeamName)

        driver.get(targetLink)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(playersTableId)))
        val playersTable = driver.findElement(By.id(playersTableId))

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("player-link")))

        val teamPlayers = playersTable.findElements(By.className("player-link"))

        val playerLink = teamPlayers.firstOrNull { it.text.equals(playerName, true) || it.text.contains(playerName, true) }
            ?.getAttribute("href") ?: throw PlayerNotFoundException(playerName)

        driver.get(playerLink)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pp-statistics")))

        val topRatedPosition = findTopRatedPosition(driver, playerLink, wait)

        driver.quit()

        return topRatedPosition
    }

    private fun findTopRatedPosition(driver: WebDriver, playerLink: String, wait: WebDriverWait): PositionRating {
        driver.get(playerLink)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pp-statistics")))
        val rows = driver.findElements(By.cssSelector("#pp-statistics table tbody tr"))

        var maxRating = Double.MIN_VALUE
        var bestPosition = ""

        for (row in rows) {
            val position = row.findElement(By.cssSelector("td.position-table-entry")).text
            val ratingText = row.findElement(By.cssSelector("td.rating")).text.replace(",", ".")
            val rating = ratingText.toDoubleOrNull() ?: continue

            if (rating > maxRating) {
                maxRating = rating
                bestPosition = position
            }
        }

        val maxRatingPosition = PositionRating(bestPosition, maxRating)

        return maxRatingPosition
    }
}