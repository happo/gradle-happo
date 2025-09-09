package com.happo.gradle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class HappoApiClient(
    private val apiKey: String,
    private val projectId: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    
    private val baseUrl = "https://happo.io/api"
    
    data class UploadResponse(
        val sha: String,
        val status: String,
        val message: String? = null
    )
    
    data class CompareResponse(
        val status: String,
        val message: String? = null,
        val diffCount: Int? = null,
        val reportUrl: String? = null
    )
    
    data class UploadRequest(
        val projectId: String,
        val branch: String,
        val commit: String,
        val screenshots: List<ScreenshotInfo>
    )
    
    data class ScreenshotInfo(
        val name: String,
        val component: String,
        val variant: String,
        val fileName: String
    )
    
    fun uploadScreenshots(
        screenshotsDir: File,
        branch: String,
        commit: String
    ): UploadResponse {
        if (!screenshotsDir.exists() || !screenshotsDir.isDirectory) {
            throw IllegalArgumentException("Screenshots directory does not exist: ${screenshotsDir.absolutePath}")
        }
        
        val screenshots = discoverScreenshots(screenshotsDir)
        if (screenshots.isEmpty()) {
            throw IllegalArgumentException("No screenshots found in directory: ${screenshotsDir.absolutePath}")
        }
        
        val uploadRequest = UploadRequest(
            projectId = projectId,
            branch = branch,
            commit = commit,
            screenshots = screenshots
        )
        
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("data", objectMapper.writeValueAsString(uploadRequest))
        
        // Add each screenshot file
        screenshots.forEach { screenshot ->
            val file = File(screenshotsDir, screenshot.fileName)
            if (file.exists()) {
                requestBody.addFormDataPart(
                    "files",
                    screenshot.fileName,
                    file.asRequestBody("image/png".toMediaType())
                )
            }
        }
        
        val request = Request.Builder()
            .url("$baseUrl/reports")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.build())
            .build()
        
        return executeRequest(request) { response ->
            objectMapper.readValue(response.body?.string(), UploadResponse::class.java)
        }
    }
    
    fun compareReports(sha1: String, sha2: String): CompareResponse {
        val request = Request.Builder()
            .url("$baseUrl/reports/compare?sha1=$sha1&sha2=$sha2")
            .addHeader("Authorization", "Bearer $apiKey")
            .get()
            .build()
        
        return executeRequest(request) { response ->
            objectMapper.readValue(response.body?.string(), CompareResponse::class.java)
        }
    }
    
    fun discoverScreenshots(screenshotsDir: File): List<ScreenshotInfo> {
        val screenshots = mutableListOf<ScreenshotInfo>()
        
        screenshotsDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension.lowercase() in listOf("png", "jpg", "jpeg")) {
                val nameWithoutExt = file.nameWithoutExtension
                // Parse filename format: component_variant.png
                val parts = nameWithoutExt.split("_", limit = 2)
                val component = parts.getOrNull(0) ?: "unknown"
                val variant = parts.getOrNull(1) ?: "default"
                
                screenshots.add(
                    ScreenshotInfo(
                        name = nameWithoutExt,
                        component = component,
                        variant = variant,
                        fileName = file.name
                    )
                )
            }
        }
        
        return screenshots
    }
    
    private fun <T> executeRequest(request: Request, responseHandler: (Response) -> T): T {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Request failed: ${response.code} ${response.message}")
            }
            return responseHandler(response)
        }
    }
}
