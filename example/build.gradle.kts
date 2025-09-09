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
    apiKey.set(project.findProperty("happo.apiKey")?.toString() ?: System.getenv("HAPPO_API_KEY") ?: "")
    projectId.set(project.findProperty("happo.projectId")?.toString() ?: System.getenv("HAPPO_PROJECT_ID") ?: "")
    screenshotsDir.set(file("src/test/screenshots"))
    branch.set(project.findProperty("happo.branch")?.toString() ?: "main")
    commit.set(project.findProperty("happo.commit")?.toString() ?: "unknown")
}

// Example of how to use the tasks
tasks.named("createHappoReport") {
    doLast {
        println("Happo report created! SHA: ${project.extensions.extraProperties.get("happo.lastReportSha")}")
    }
}
