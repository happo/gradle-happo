package com.happo.gradle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class HappoApiClientTest {

        @TempDir lateinit var tempDir: File

        private lateinit var mockWebServer: MockWebServer
        private lateinit var objectMapper: ObjectMapper
        private lateinit var client: HappoApiClient

        @BeforeEach
        fun setUp() {
                mockWebServer = MockWebServer()
                mockWebServer.start()

                objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

                val httpClient = OkHttpClient.Builder().build()
                client =
                        HappoApiClient(
                                apiKey = "test-api-key",
                                apiSecret = "test-secret",
                                project = "test-project",
                                client = httpClient
                        )
        }

        @AfterEach
        fun tearDown() {
                mockWebServer.shutdown()
        }

        @Test
        fun `should discover screenshots from directory`() {
                // Create test screenshots
                val screenshotsDir = File(tempDir, "screenshots")
                screenshotsDir.mkdirs()

                createTestPngFile(File(screenshotsDir, "Button_primary.png"))
                createTestPngFile(File(screenshotsDir, "Button_secondary.png"))
                createTestPngFile(File(screenshotsDir, "Card_default.png"))
                createTestPngFile(File(screenshotsDir, "Card_elevated.png"))
                File(screenshotsDir, "README.md").createNewFile() // Should be ignored

                val screenshots = client.discoverScreenshots(screenshotsDir)

                assertEquals(4, screenshots.size)
                assertTrue(screenshots.any { it.component == "Button" && it.variant == "primary" })
                assertTrue(
                        screenshots.any { it.component == "Button" && it.variant == "secondary" }
                )
                assertTrue(screenshots.any { it.component == "Card" && it.variant == "default" })
                assertTrue(screenshots.any { it.component == "Card" && it.variant == "elevated" })
        }

        @Test
        fun `should handle empty screenshots directory`() {
                val screenshotsDir = File(tempDir, "empty")
                screenshotsDir.mkdirs()

                val screenshots = client.discoverScreenshots(screenshotsDir)

                assertTrue(screenshots.isEmpty())
        }

        @Test
        fun `can create a report`() {
                val screenshotsDir = File(tempDir, "screenshots")
                screenshotsDir.mkdirs()
                createTestPngFile(File(screenshotsDir, "Button_primary.png"))

                // Mock the image upload URL response
                val imageUploadResponse =
                        HappoApiClient.ImageUploadResponse(
                                uploadUrl = null,
                                url = "https://happo.io/images/test-hash.png",
                                message = null
                        )
                mockWebServer.enqueue(
                        MockResponse()
                                .setResponseCode(200)
                                .setHeader("Content-Type", "application/json")
                                .setBody(objectMapper.writeValueAsString(imageUploadResponse))
                )

                // Mock the report creation response
                val reportResponse =
                        HappoApiClient.UploadResponse(url = "https://happo.io/reports/test-sha")
                mockWebServer.enqueue(
                        MockResponse()
                                .setResponseCode(200)
                                .setHeader("Content-Type", "application/json")
                                .setBody(objectMapper.writeValueAsString(reportResponse))
                )

                // Create a client that uses the mock server URL
                val httpClient = OkHttpClient.Builder().build()
                val testClient =
                        HappoApiClient(
                                apiKey = "test-api-key",
                                apiSecret = "test-secret",
                                project = "test-project",
                                client = httpClient,
                                baseUrl = mockWebServer.url("/").toString()
                        )

                val response = testClient.createReport(screenshotsDir, "test-sha")

                assertEquals("https://happo.io/reports/test-sha", response.url)
        }

        @Test
        fun `can compare reports`() {
                // Mock the compare response
                val compareResponse =
                        HappoApiClient.CompareResponse(
                                equal = true,
                                summary = "All screenshots match"
                        )
                mockWebServer.enqueue(
                        MockResponse()
                                .setResponseCode(200)
                                .setHeader("Content-Type", "application/json")
                                .setBody(objectMapper.writeValueAsString(compareResponse))
                )

                val testClient =
                        HappoApiClient(
                                apiKey = "test-api-key",
                                apiSecret = "test-secret",
                                project = "test-project",
                                client = OkHttpClient.Builder().build(),
                                baseUrl = mockWebServer.url("/").toString()
                        )

                val response = testClient.compareReports("sha1", "sha2")

                assertEquals(true, response.equal)
                assertEquals("All screenshots match", response.summary)
        }

        private fun createTestPngFile(file: File) {
                // Copy an existing PNG file from the example directory
                val examplePngFile = File("example/src/test/screenshots/Button_primary.png")
                examplePngFile.copyTo(file, overwrite = true)
        }
}
