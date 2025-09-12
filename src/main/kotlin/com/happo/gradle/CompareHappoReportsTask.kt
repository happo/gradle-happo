package com.happo.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class CompareHappoReportsTask : DefaultTask() {

    @get:Input abstract val apiKey: Property<String>

    @get:Input abstract val apiSecret: Property<String>

    @get:Input abstract val projectName: Property<String>

    @get:Input abstract val baseUrl: Property<String>

    @get:Input @get:Optional abstract val link: Property<String>

    @get:Input @get:Optional abstract val message: Property<String>

    @get:Input abstract val baseBranch: Property<String>

    @TaskAction
    fun compareReports() {
        val apiKey = apiKey.get()
        val apiSecret = apiSecret.get()
        val projectName = projectName.get()
        val baseUrl = baseUrl.get()
        val link = link.orNull
        val message = message.orNull
        val baseBranch = baseBranch.get()

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
        var firstSha = gitHelper.findBaselineSha(baseBranch = baseBranch)
        val secondSha = gitHelper.findHEADSha()
        if (firstSha == secondSha) {
            firstSha = gitHelper.findBaselineSha("HEAD^", baseBranch)
        }
        val fallbackShas = gitHelper.findFallbackShas(firstSha)
        // Use git commit subject as default message if not provided
        val reportMessage = message ?: gitHelper.getCommitSubject(secondSha)
        val reportLink = link ?: gitHelper.getCommitLink()

        logger.lifecycle("Comparing Happo reports...")
        logger.lifecycle("Project: $projectName")
        logger.lifecycle("First SHA: $firstSha")
        logger.lifecycle("Second SHA: $secondSha")
        logger.lifecycle("Fallback SHAs: $fallbackShas")
        logger.lifecycle("Message: $reportMessage")
        link?.let { logger.lifecycle("Link: $it") }

        try {
            val apiClient = HappoApiClient(apiKey, apiSecret, projectName, baseUrl = baseUrl)
            val response = apiClient.compareReports(firstSha, secondSha, reportLink, reportMessage)

            logger.lifecycle("✅ Comparison created")
            logger.lifecycle(response.compareUrl)
        } catch (e: Exception) {
            logger.error("❌ Failed to compare Happo reports: ${e.message}")
            throw e
        }
    }
}
