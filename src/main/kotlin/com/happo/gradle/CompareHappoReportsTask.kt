package com.happo.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class CompareHappoReportsTask : DefaultTask() {

    @get:Input abstract val apiKey: Property<String>

    @get:Input abstract val apiSecret: Property<String>

    @get:Input abstract val projectName: Property<String>

    @get:Input
    @Option(option = "sha1", description = "Commit sha of the baseline report")
    var sha1: String? = null

    @get:Input
    @Option(option = "sha2", description = "Commit sha of the new report")
    var sha2: String? = null

    @TaskAction
    fun compareReports() {
        val apiKey = apiKey.get()
        val apiSecret = apiSecret.get()
        val projectName = projectName.get()

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

        val firstSha =
                sha1
                        ?: throw IllegalArgumentException(
                                "Baseline commit SHA is required. Use --sha1 option."
                        )
        val secondSha =
                sha2
                        ?: throw IllegalArgumentException(
                                "New commit SHA is required. Use --sha2 option."
                        )

        logger.lifecycle("Comparing Happo reports...")
        logger.lifecycle("Project: $projectName")
        logger.lifecycle("First SHA: $firstSha")
        logger.lifecycle("Second SHA: $secondSha")

        try {
            val apiClient = HappoApiClient(apiKey, apiSecret, projectName)
            val response = apiClient.compareReports(firstSha, secondSha)

            logger.lifecycle("✅ Comparison completed!")
            logger.lifecycle(response.summary)
            if (response.equal) {
                logger.lifecycle("✅ No differences found")
            } else {
                logger.lifecycle("⚠️ Found diffs")
            }
        } catch (e: Exception) {
            logger.error("❌ Failed to compare Happo reports: ${e.message}")
            throw e
        }
    }
}
