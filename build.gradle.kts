import java.util.Properties

plugins {
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "io.happo"
version = "1.0.0"

// Load local properties file if it exists
val localPropertiesFile = file("gradle-local.properties")
if (localPropertiesFile.exists()) {
    val localProperties = Properties()
    localPropertiesFile.inputStream().use { localProperties.load(it) }
    // Add local properties to project properties so they can be accessed by plugins
    for ((key, value) in localProperties) {
        if (project.findProperty(key.toString()) == null) {
            project.ext.set(key.toString(), value.toString())
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.21")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.21")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Gradle Plugin Portal configuration
gradlePlugin {
    website.set("https://github.com/happo/gradle-happo")
    vcsUrl.set("https://github.com/happo/gradle-happo.git")
    plugins {
        create("happo") {
            id = "io.happo.gradle"
            implementationClass = "io.happo.gradle.HappoPlugin"
            displayName = "Happo Gradle Plugin"
            description = "A Gradle plugin for uploading and comparing Happo visual regression test reports"
            tags.set(listOf("happo", "visual-regression", "testing", "screenshots"))
        }
    }
}
