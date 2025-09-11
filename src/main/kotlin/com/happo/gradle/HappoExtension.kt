package com.happo.gradle

import java.io.File
import org.gradle.api.provider.Property

abstract class HappoExtension {
    abstract val apiKey: Property<String>
    abstract val apiSecret: Property<String>
    abstract val projectName: Property<String>
    abstract val screenshotsDir: Property<File>
    abstract val sha: Property<String>
    abstract val baseUrl: Property<String>
    abstract val link: Property<String>
    abstract val message: Property<String>

    init {
        // Set default values
        screenshotsDir.convention(File("screenshots"))
        baseUrl.convention("https://happo.io")
        projectName.convention("default")
    }
}
