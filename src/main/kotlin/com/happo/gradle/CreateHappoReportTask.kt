package com.happo.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CreateHappoReportTask : DefaultTask() {
    
    @get:Input
    abstract val apiKey: Property<String>
    
    @get:Input
    abstract val projectId: Property<String>
    
    @get:InputDirectory
    abstract val screenshotsDir: Property<File>
    
    @get:Input
    abstract val branch: Property<String>
    
    @get:Input
    abstract val commit: Property<String>
    
    @TaskAction
    fun createReport() {
        val apiKey = apiKey.get()
        val projectId = projectId.get()
        val screenshotsDir = screenshotsDir.get()
        val branch = branch.get()
        val commit = commit.get()
        
        if (apiKey.isBlank()) {
            throw IllegalArgumentException("Happo API key is required. Set it in the happo extension or via happo.apiKey property.")
        }
        
        if (projectId.isBlank()) {
            throw IllegalArgumentException("Happo project ID is required. Set it in the happo extension or via happo.projectId property.")
        }
        
        logger.lifecycle("Creating Happo report...")
        logger.lifecycle("Project ID: $projectId")
        logger.lifecycle("Branch: $branch")
        logger.lifecycle("Commit: $commit")
        logger.lifecycle("Screenshots directory: ${screenshotsDir.absolutePath}")
        
        try {
            val apiClient = HappoApiClient(apiKey, projectId)
            val response = apiClient.uploadScreenshots(screenshotsDir, branch, commit)
            
            logger.lifecycle("✅ Happo report created successfully!")
            logger.lifecycle("Report SHA: ${response.sha}")
            logger.lifecycle("Status: ${response.status}")
            response.message?.let { logger.lifecycle("Message: $it") }
            
            // Store the SHA for potential future use
            project.extensions.extraProperties.set("happo.lastReportSha", response.sha)
            
        } catch (e: Exception) {
            logger.error("❌ Failed to create Happo report: ${e.message}")
            throw e
        }
    }
}
