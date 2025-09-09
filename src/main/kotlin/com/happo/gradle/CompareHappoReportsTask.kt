package com.happo.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class CompareHappoReportsTask : DefaultTask() {
    
    @get:Input
    abstract val apiKey: Property<String>
    
    @get:Input
    abstract val projectId: Property<String>
    
    @get:Input
    @Option(option = "sha1", description = "First report SHA1 to compare")
    var sha1: String? = null
    
    @get:Input
    @Option(option = "sha2", description = "Second report SHA1 to compare")
    var sha2: String? = null
    
    @TaskAction
    fun compareReports() {
        val apiKey = apiKey.get()
        val projectId = projectId.get()
        
        if (apiKey.isBlank()) {
            throw IllegalArgumentException("Happo API key is required. Set it in the happo extension or via happo.apiKey property.")
        }
        
        if (projectId.isBlank()) {
            throw IllegalArgumentException("Happo project ID is required. Set it in the happo extension or via happo.projectId property.")
        }
        
        val firstSha = sha1 ?: throw IllegalArgumentException("First SHA1 is required. Use --sha1 option.")
        val secondSha = sha2 ?: throw IllegalArgumentException("Second SHA1 is required. Use --sha2 option.")
        
        logger.lifecycle("Comparing Happo reports...")
        logger.lifecycle("Project ID: $projectId")
        logger.lifecycle("First SHA: $firstSha")
        logger.lifecycle("Second SHA: $secondSha")
        
        try {
            val apiClient = HappoApiClient(apiKey, projectId)
            val response = apiClient.compareReports(firstSha, secondSha)
            
            logger.lifecycle("✅ Comparison completed!")
            logger.lifecycle("Status: ${response.status}")
            response.message?.let { logger.lifecycle("Message: $it") }
            response.diffCount?.let { 
                if (it > 0) {
                    logger.lifecycle("⚠️  Found $it differences")
                } else {
                    logger.lifecycle("✅ No differences found")
                }
            }
            response.reportUrl?.let { 
                logger.lifecycle("Report URL: $it")
            }
            
        } catch (e: Exception) {
            logger.error("❌ Failed to compare Happo reports: ${e.message}")
            throw e
        }
    }
}
