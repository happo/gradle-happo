buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("io.happo:gradle-happo:1.0.0")
    }
}

apply(plugin = "io.happo.gradle")

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure the Happo extension
project.extensions.configure<io.happo.gradle.HappoExtension>("happo") {
    apiKey.set(System.getenv("HAPPO_API_KEY") ?: "foobar")
    apiSecret.set(System.getenv("HAPPO_API_SECRET") ?: "foobar")
    projectName.set("default")
    screenshotsDir.set(file("src/test/screenshots"))
    baseBranch.set("origin/main")
}