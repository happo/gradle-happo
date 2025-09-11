package com.happo.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class HappoPlugin : Plugin<Project> {
        override fun apply(project: Project) {
                // Create the Happo extension
                val happoExtension = project.extensions.create("happo", HappoExtension::class.java)

                // Configure extension properties to read from project properties
                happoExtension.apiKey.set(
                        project.provider {
                                project.findProperty("happo.apiKey")?.toString()
                                        ?: System.getenv("HAPPO_API_KEY") ?: ""
                        }
                )
                happoExtension.apiSecret.set(
                        project.provider {
                                project.findProperty("happo.apiSecret")?.toString()
                                        ?: System.getenv("HAPPO_API_SECRET") ?: ""
                        }
                )
                happoExtension.projectName.set(
                        project.provider {
                                project.findProperty("happo.projectName")?.toString()
                                        ?: System.getenv("HAPPO_PROJECT_NAME") ?: ""
                        }
                )
                happoExtension.baseUrl.set(
                        project.provider {
                                project.findProperty("happo.baseUrl")?.toString()
                                        ?: System.getenv("HAPPO_BASE_URL") ?: "https://happo.io"
                        }
                )
                happoExtension.link.set(
                        project.provider {
                                project.findProperty("happo.link")?.toString()
                                        ?: System.getenv("HAPPO_LINK") ?: ""
                        }
                )
                happoExtension.message.set(
                        project.provider {
                                project.findProperty("happo.message")?.toString()
                                        ?: System.getenv("HAPPO_MESSAGE") ?: null
                        }
                )

                // Register the createHappoReport task
                val createHappoReportTask =
                        project.tasks.register(
                                "createHappoReport",
                                CreateHappoReportTask::class.java
                        ) { task ->
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
                                task.description =
                                        "Compare two Happo reports by their SHA1 identifiers"
                        }

                // Configure tasks with extension values
                project.afterEvaluate {
                        createHappoReportTask.configure { task ->
                                task.apiKey.set(happoExtension.apiKey)
                                task.apiSecret.set(happoExtension.apiSecret)
                                task.projectName.set(happoExtension.projectName)
                                task.screenshotsDir.set(happoExtension.screenshotsDir)
                                task.baseUrl.set(happoExtension.baseUrl)
                                task.link.set(happoExtension.link)
                                task.message.set(happoExtension.message)
                        }

                        compareHappoReportsTask.configure { task ->
                                task.apiKey.set(happoExtension.apiKey)
                                task.apiSecret.set(happoExtension.apiSecret)
                                task.projectName.set(happoExtension.projectName)
                                task.baseUrl.set(happoExtension.baseUrl)
                        }
                }
        }
}
