package com.example.unq_dapps_2025_01_grupo_c.service

import com.example.unq_dapps_2025_01_grupo_c.dto.team.AdvancedMetricsResponse
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
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration

@Service
class WhoScoredService {

    val teamLinkClassName = "team-link"
    val standingsClassName = "standings"
    val playersTableId = "player-table-statistics-body"
    val baseUrl = "https://es.whoscored.com"

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

    fun fetchTeamsStats(teamNameOne: String, teamNameTwo: String): Pair<Map<String, String>, Map<String, String>> {
        val driver: WebDriver = createDriver()
        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        try {
            driver.get("$baseUrl/regions/252/tournaments/2/inglaterra-premier-league")
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(standingsClassName)))
            val teamsTable = driver.findElement(By.className(standingsClassName))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(teamLinkClassName)))
            val links = teamsTable.findElements(By.className(teamLinkClassName))

            val linkOne = links.firstOrNull { it.text.equals(teamNameOne, true) || it.text.contains(teamNameOne, true) }
                ?.getAttribute("href") ?: throw TeamNotFoundException(teamNameOne)

            driver.get(linkOne)
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("statistics-team-table-summary")))
            val statsOne = extractTeamStats(driver)

            driver.get("$baseUrl/regions/252/tournaments/2/inglaterra-premier-league")
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(standingsClassName)))
            val teamsTable2 = driver.findElement(By.className(standingsClassName))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(teamLinkClassName)))
            val links2 = teamsTable2.findElements(By.className(teamLinkClassName))

            val linkTwo = links2.firstOrNull { it.text.equals(teamNameTwo, true) || it.text.contains(teamNameTwo, true) }
                ?.getAttribute("href") ?: throw TeamNotFoundException(teamNameTwo)

            driver.get(linkTwo)
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("statistics-team-table-summary")))
            val statsTwo = extractTeamStats(driver)

            return Pair(statsOne, statsTwo)
        } finally {
            driver.quit()
        }
    }

    private fun extractTeamStats(driver: WebDriver): Map<String, String> {
        val stats = mutableMapOf<String, String>()
        try {
            val table = driver.findElement(By.id("top-team-stats-summary-grid"))
            val rows = table.findElements(By.cssSelector("tbody tr"))
            val totalRow = rows.last()
            val cells = totalRow.findElements(By.tagName("td"))

            fun safeText(cell: org.openqa.selenium.WebElement): String = try { cell.text.trim() } catch (e: Exception) { "N/A" }

            stats["apps"]          = safeText(cells[1])
            stats["goals"]         = safeText(cells[2])
            stats["shotsPerGame"]  = safeText(cells[3])
            stats["yellowCards"]   = safeText(cells[4].findElement(By.className("yellow-card-box")))
            stats["redCards"]      = safeText(cells[4].findElement(By.className("red-card-box")))
            stats["possession"]    = safeText(cells[5])
            stats["passSuccess"]   = safeText(cells[6])
            stats["aerialWon"]     = safeText(cells[7])
            stats["rating"]        = safeText(cells[8])

        } catch (e: Exception) {
            throw RuntimeException("Stats not found or table format changed", e)
        }
        return stats
    }

    fun fetchAdvancedMetrics(teamName: String): AdvancedMetricsResponse {
        val driver = createDriver()
        val wait = WebDriverWait(driver, Duration.ofSeconds(120))
        try {
            driver.get("$baseUrl/regions/252/tournaments/2/inglaterra-premier-league")
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(standingsClassName)))
            val teamsTable = driver.findElement(By.className(standingsClassName))
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(teamLinkClassName)))
            val links = teamsTable.findElements(By.className(teamLinkClassName))

            val foundTeamElement = links.firstOrNull { it.text.equals(teamName, true) || it.text.contains(teamName, true) }
                ?: throw TeamNotFoundException(teamName)
            val link = foundTeamElement.getAttribute("href")
            val foundTeamName = foundTeamElement.text

            if (link != null) {
                driver.get(link)
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("statistics-team-table-summary")))
            val stats = extractTeamStats(driver)

            fun safeDouble(value: String?): Double? =
                value?.replace(",", ".")?.toDoubleOrNull()

            val totalApps = safeDouble(stats["apps"])
            val totalGoals = safeDouble(stats["goals"])
            val shotsPerGame = safeDouble(stats["shotsPerGame"])
            val yellowCards = safeDouble(stats["yellowCards"])
            val redCards = safeDouble(stats["redCards"])
            val passSuccess = safeDouble(stats["passSuccess"])
            val rating = safeDouble(stats["rating"])

            val goalPerGame = if (totalApps != null && totalApps > 0 && totalGoals != null) totalGoals / totalApps else null
            val cardsPerGame = if (totalApps != null && totalApps > 0 && yellowCards != null && redCards != null) (yellowCards + redCards) / totalApps else null
            val shotConversionRate = if (shotsPerGame != null && shotsPerGame > 0 && totalGoals != null) totalGoals / shotsPerGame else null
            val passToGoalRatio = if (passSuccess != null && totalGoals != null && totalGoals > 0) passSuccess / totalGoals else null
            val disciplineScore = if (totalApps != null && totalApps > 0 && yellowCards != null && redCards != null) (yellowCards + 2 * redCards) / totalApps else null
            val averageRating = rating

            return AdvancedMetricsResponse(
                team = foundTeamName,
                goalPerGame = goalPerGame.rounded(2),
                cardsPerGame = cardsPerGame.rounded(2),
                shotConversionRate = shotConversionRate.rounded(2),
                passToGoalRatio = passToGoalRatio.rounded(2),
                disciplineScore = disciplineScore.rounded(2),
                averageRating = averageRating.rounded(2),
                rawStats = stats
            )
        } finally {
            driver.quit()
        }
    }


    private fun Double?.rounded(decimals: Int = 2): Double? =
        this?.let { BigDecimal(it).setScale(decimals, RoundingMode.HALF_UP).toDouble() }
}