package com.happo.gradle

import org.gradle.api.provider.Property
import java.io.File

abstract class HappoExtension {
    abstract val apiKey: Property<String>
    abstract val projectId: Property<String>
    abstract val screenshotsDir: Property<File>
    abstract val branch: Property<String>
    abstract val commit: Property<String>
    
    init {
        // Set default values
        screenshotsDir.convention(File("screenshots"))
        branch.convention("main")
        commit.convention("unknown")
    }
}
