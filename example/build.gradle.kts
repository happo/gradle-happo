buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("com.happo:gradle-happo:1.0.0")
    }
}

apply(plugin = "com.happo.gradle")

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure the Happo extension
project.extensions.configure<com.happo.gradle.HappoExtension>("happo") {
    apiKey.set(System.getenv("HAPPO_API_KEY") ?: "foobar")
    apiSecret.set(System.getenv("HAPPO_API_SECRET") ?: "foobar")
    projectName.set("default")
    screenshotsDir.set(file("src/test/screenshots"))
}