package com.happo.gradle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class HappoApiClient(
        private val apiKey: String,
        private val apiSecret: String,
        private val project: String
) {
    private val client =
            OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    private val baseUrl = "https://happo.io/"

    data class UploadResponse(
            val url: String,
    )

    data class CompareResponse(val equal: Boolean, val summary: String)

    data class ImageUploadResponse(
            val uploadUrl: String? = null,
            val url: String,
            val message: String? = null
    )

    data class UploadRequest(val project: String, val snaps: List<ScreenshotInfo>)

    data class ScreenshotInfo(
            val component: String,
            val variant: String,
            val target: String,
            val height: Int,
            val width: Int,
            val url: String,
            val fileName: String
    )

    fun createReport(screenshotsDir: File, sha: String): UploadResponse {
        if (!screenshotsDir.exists() || !screenshotsDir.isDirectory) {
            throw IllegalArgumentException(
                    "Screenshots directory does not exist: ${screenshotsDir.absolutePath}"
            )
        }

        val screenshots = discoverScreenshots(screenshotsDir)
        if (screenshots.isEmpty()) {
            throw IllegalArgumentException(
                    "No screenshots found in directory: ${screenshotsDir.absolutePath}"
            )
        }

        // Upload each screenshot and get their URLs
        val screenshotsWithUrls =
                screenshots.map { screenshot ->
                    val file = File(screenshotsDir, screenshot.fileName)
                    if (!file.exists()) {
                        throw IllegalArgumentException(
                                "Screenshot file does not exist: ${file.absolutePath}"
                        )
                    }
                    val hash = ImageFileHashCalculator().calculate(file)
                    val imageUrl = uploadImage(file, hash)
                    screenshot.copy(url = imageUrl)
                }

        val uploadRequest = UploadRequest(project = project, snaps = screenshotsWithUrls)

        val requestBody =
                objectMapper
                        .writeValueAsString(uploadRequest)
                        .toRequestBody("application/json".toMediaType())

        val request =
                Request.Builder()
                        .url("$baseUrl/api/reports/$sha")
                        .addHeader("Authorization", "Basic $apiKey:$apiSecret")
                        .post(requestBody)
                        .build()

        return executeRequest(request) { response: Response ->
            objectMapper.readValue(response.body?.string(), UploadResponse::class.java)
        }
    }

    fun compareReports(sha1: String, sha2: String): CompareResponse {
        val request =
                Request.Builder()
                        .url("$baseUrl/api/reports/$sha1/compare/$sha2")
                        .addHeader("Authorization", "Basic $apiKey:$apiSecret")
                        .get()
                        .build()

        return executeRequest(request) { response: Response ->
            objectMapper.readValue(response.body?.string(), CompareResponse::class.java)
        }
    }

    fun getImageUploadUrl(hash: String): ImageUploadResponse {
        val request =
                Request.Builder()
                        .url("$baseUrl/api/images/$hash/upload-url")
                        .addHeader("Authorization", "Basic $apiKey:$apiSecret")
                        .get()
                        .build()

        return executeRequest(request) { response: Response ->
            objectMapper.readValue(response.body?.string(), ImageUploadResponse::class.java)
        }
    }

    fun uploadImageFile(uploadUrl: String, imageFile: File): String {
        val requestBody = imageFile.asRequestBody("image/png".toMediaType())

        val request = Request.Builder().url(uploadUrl).post(requestBody).build()

        return executeRequest(request) { response: Response ->
            val responseBody = response.body?.string()
            // The response should contain the final image URL
            objectMapper.readValue(responseBody, ImageUploadResponse::class.java).url
        }
    }

    fun uploadImage(imageFile: File, hash: String): String {
        val uploadResponse = getImageUploadUrl(hash)

        return if (uploadResponse.uploadUrl != null) {
            // Image needs to be uploaded
            uploadImageFile(uploadResponse.uploadUrl, imageFile)
        } else {
            // Image already exists, return the existing URL
            uploadResponse.url
        }
    }

    fun discoverScreenshots(screenshotsDir: File): List<ScreenshotInfo> {
        val screenshots = mutableListOf<ScreenshotInfo>()

        screenshotsDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension.lowercase().equals("png")) {
                val nameWithoutExt = file.nameWithoutExtension
                // Parse filename format: component_variant.png
                val parts = nameWithoutExt.split("_", limit = 3)
                val component = parts.getOrNull(0) ?: "unknown"
                val variant = parts.getOrNull(1) ?: "default"
                val target = parts.getOrNull(2) ?: "device"
                val width = 500
                val height = 500

                screenshots.add(
                        ScreenshotInfo(
                                component = component,
                                variant = variant,
                                target = target,
                                height = height,
                                width = width,
                                url = "", // Will be set after upload
                                fileName = file.name
                        )
                )
            }
        }

        return screenshots
    }

    private fun <T> executeRequest(request: Request, responseHandler: (Response) -> T): T {
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Request failed: ${response.code} ${response.message}")
            }
            responseHandler(response)
        }
    }
}
