package com.happo.gradle

import java.io.File
import org.gradle.api.provider.Property

abstract class HappoExtension {
    abstract val apiKey: Property<String>
    abstract val apiSecret: Property<String>
    abstract val project: Property<String>
    abstract val screenshotsDir: Property<File>

    init {
        // Set default values
        screenshotsDir.convention(File("screenshots"))
    }
}
