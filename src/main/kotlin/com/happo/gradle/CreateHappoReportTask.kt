package com.happo.gradle

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CreateHappoReportTask : DefaultTask() {

    @get:Input abstract val apiKey: Property<String>

    @get:Input abstract val apiSecret: Property<String>

    @get:Input abstract val project: Property<String>

    @get:InputDirectory abstract val screenshotsDir: Property<File>

    @get:Input abstract val sha: Property<String>

    @TaskAction
    fun createReport() {
        val apiKey = apiKey.get()
        val apiSecret = apiSecret.get()
        val project = project.get()
        val screenshotsDir = screenshotsDir.get()
        val sha = sha.get()

        if (apiKey.isBlank()) {
            throw IllegalArgumentException(
                    "Happo API key is required. Set it in the happo extension or via happo.apiKey property."
            )
        }

        if (apiSecret.isBlank()) {
            throw IllegalArgumentException(
                    "Happo API secret is required. Set it in the happo extension or via happo.apiSecret property."
            )
        }

        logger.lifecycle("Creating Happo report...")
        logger.lifecycle("Project: $project")
        logger.lifecycle("SHA: $sha")
        logger.lifecycle("Screenshots directory: ${screenshotsDir.absolutePath}")

        try {
            val apiClient = HappoApiClient(apiKey, apiSecret, project)
            val response = apiClient.createReport(screenshotsDir, sha)

            logger.lifecycle("✅ Happo report created successfully!")
            logger.lifecycle("URL: ${response.url}")
        } catch (e: Exception) {
            logger.error("❌ Failed to create Happo report: ${e.message}")
            throw e
        }
    }
}
