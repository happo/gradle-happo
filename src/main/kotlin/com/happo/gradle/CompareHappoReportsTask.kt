package com.happo.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CompareHappoReportsTask : DefaultTask() {

    @get:Input abstract val apiKey: Property<String>

    @get:Input abstract val apiSecret: Property<String>

    @get:Input abstract val projectName: Property<String>

    @get:Input abstract val baseUrl: Property<String>

    @TaskAction
    fun compareReports() {
        val apiKey = apiKey.get()
        val apiSecret = apiSecret.get()
        val projectName = projectName.get()
        val baseUrl = baseUrl.get()

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

        val gitHelper = GitHelper()
        var firstSha = gitHelper.findBaselineSha()
        val secondSha = gitHelper.findHEADSha()
        if (firstSha == secondSha) {
            firstSha = gitHelper.findBaselineSha("HEAD^")
        }
        val fallbackShas = gitHelper.findFallbackShas(firstSha)

        logger.lifecycle("Comparing Happo reports...")
        logger.lifecycle("Project: $projectName")
        logger.lifecycle("First SHA: $firstSha")
        logger.lifecycle("Second SHA: $secondSha")
        logger.lifecycle("Fallback SHAs: $fallbackShas")

        try {
            val apiClient = HappoApiClient(apiKey, apiSecret, projectName, baseUrl = baseUrl)
            val response = apiClient.compareReports(firstSha, secondSha)

            logger.lifecycle("✅ Comparison created successfully!")
            logger.lifecycle(response.compareUrl)
        } catch (e: Exception) {
            logger.error("❌ Failed to compare Happo reports: ${e.message}")
            throw e
        }
    }
}
