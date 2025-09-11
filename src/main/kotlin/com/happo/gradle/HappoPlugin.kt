package com.happo.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class HappoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Create the Happo extension
        val happoExtension = project.extensions.create("happo", HappoExtension::class.java)

        // Register the createHappoReport task
        val createHappoReportTask =
                project.tasks.register("createHappoReport", CreateHappoReportTask::class.java) {
                        task ->
                    task.group = "happo"
                    task.description = "Upload screenshots to Happo and create a report"
                }

        // Register the compareHappoReports task
        val compareHappoReportsTask =
                project.tasks.register(
                        "compareHappoReports",
                        CompareHappoReportsTask::class.java
                ) { task ->
                    task.group = "happo"
                    task.description = "Compare two Happo reports by their SHA1 identifiers"
                }

        // Configure tasks with extension values
        project.afterEvaluate {
            createHappoReportTask.configure { task ->
                task.apiKey.set(happoExtension.apiKey)
                task.apiSecret.set(happoExtension.apiSecret)
                task.projectName.set(happoExtension.projectName)
                task.screenshotsDir.set(happoExtension.screenshotsDir)
            }

            compareHappoReportsTask.configure { task ->
                task.apiKey.set(happoExtension.apiKey)
                task.apiSecret.set(happoExtension.apiSecret)
                task.projectName.set(happoExtension.projectName)
            }
        }
    }
}
