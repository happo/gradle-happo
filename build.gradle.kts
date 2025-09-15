plugins {
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    `maven-publish`
    signing
}

group = "io.happo"
version = "1.0.0"

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

gradlePlugin {
    plugins {
        create("happo") {
            id = "io.happo.gradle"
            implementationClass = "io.happo.gradle.HappoPlugin"
            displayName = "Happo Gradle Plugin"
            description = "A Gradle plugin for uploading and comparing Happo visual regression test reports"
        }
    }
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

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            groupId = project.group.toString()
            artifactId = "gradle-happo"
            version = project.version.toString()
            
            pom {
                name.set("Happo Gradle Plugin")
                description.set("A Gradle plugin for uploading and comparing Happo visual regression test reports")
                url.set("https://github.com/happo/gradle-happo")
                
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("happo")
                        name.set("Happo Team")
                        email.set("support@happo.io")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/happo/gradle-happo.git")
                    developerConnection.set("scm:git:ssh://github.com:happo/gradle-happo.git")
                    url.set("https://github.com/happo/gradle-happo")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String?
                password = project.findProperty("ossrhPassword") as String?
            }
        }
    }
}

// Signing configuration
signing {
    sign(publishing.publications["maven"])
}
