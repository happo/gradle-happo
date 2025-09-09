package com.happo.gradle

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class HappoApiClientTest {

    @TempDir lateinit var tempDir: File

    @Test
    fun `should discover screenshots from directory`() {
        // Create test screenshots
        val screenshotsDir = File(tempDir, "screenshots")
        screenshotsDir.mkdirs()

        File(screenshotsDir, "Button_primary.png").createNewFile()
        File(screenshotsDir, "Button_secondary.png").createNewFile()
        File(screenshotsDir, "Card_default.png").createNewFile()
        File(screenshotsDir, "Card_elevated.png").createNewFile()
        File(screenshotsDir, "README.md").createNewFile() // Should be ignored

        val client = HappoApiClient("test-api-key", "test-secret", "test-project")
        val screenshots = client.discoverScreenshots(screenshotsDir)

        assertEquals(4, screenshots.size)
        assertTrue(screenshots.any { it.component == "Button" && it.variant == "primary" })
        assertTrue(screenshots.any { it.component == "Button" && it.variant == "secondary" })
        assertTrue(screenshots.any { it.component == "Card" && it.variant == "default" })
        assertTrue(screenshots.any { it.component == "Card" && it.variant == "elevated" })
    }

    @Test
    fun `should handle empty screenshots directory`() {
        val screenshotsDir = File(tempDir, "empty")
        screenshotsDir.mkdirs()

        val client = HappoApiClient("test-api-key", "test-secret", "test-project")
        val screenshots = client.discoverScreenshots(screenshotsDir)

        assertTrue(screenshots.isEmpty())
    }
}
