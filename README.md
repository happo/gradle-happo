# Gradle Happo Plugin

A Gradle plugin for uploading and comparing Happo visual regression test reports.

## Features

- **Upload Screenshots**: Upload screenshots from a specified folder to Happo
- **Compare Reports**: Compare two Happo reports by their SHA1 identifiers
- **Flexible Configuration**: Support for environment variables and project properties
- **Screenshot Discovery**: Automatically discovers screenshots in the specified directory

## Usage

### Apply the Plugin

```kotlin
plugins {
    id("com.happo.gradle")
}
```

### Configuration

Configure the plugin using the `happo` extension:

```kotlin
happo {
    apiKey = "your-happo-api-key"
    projectId = "your-happo-project-id"
    screenshotsDir = file("src/test/screenshots")
    branch = "main"
    commit = "abc123"
}
```

You can also use environment variables or project properties:

```kotlin
happo {
    apiKey = project.findProperty("happo.apiKey")?.toString() ?: System.getenv("HAPPO_API_KEY") ?: ""
    projectId = project.findProperty("happo.projectId")?.toString() ?: System.getenv("HAPPO_PROJECT_ID") ?: ""
    screenshotsDir = file("src/test/screenshots")
    branch = project.findProperty("happo.branch")?.toString() ?: "main"
    commit = project.findProperty("happo.commit")?.toString() ?: "unknown"
}
```

### Tasks

#### createHappoReport

Uploads all screenshots from the configured directory to Happo and creates a report.

```bash
./gradlew createHappoReport
```

This task will:

- Discover all PNG/JPG images in the screenshots directory
- Parse component and variant names from filenames (format: `component_variant.png`)
- Upload screenshots to Happo
- Create a report and return the SHA1 identifier

#### compareHappoReports

Compares two Happo reports by their SHA1 identifiers.

```bash
./gradlew compareHappoReports --sha1 <first-sha> --sha2 <second-sha>
```

This task will:

- Compare the two reports
- Show the number of differences found
- Provide a report URL if available

### Screenshot Naming Convention

The plugin expects screenshots to be named using the format: `component_variant.png`

Examples:

- `Button_primary.png` → component: "Button", variant: "primary"
- `Card_default.png` → component: "Card", variant: "default"
- `Modal_large.png` → component: "Modal", variant: "large"

## Installation

### Using the Plugin Portal

```kotlin
plugins {
    id("com.happo.gradle") version "1.0.0"
}
```

### Using a Local Build

1. Clone this repository
2. Build the plugin: `./gradlew build`
3. Publish to local Maven: `./gradlew publishToMavenLocal`
4. Use in your project:

```kotlin
plugins {
    id("com.happo.gradle") version "1.0.0"
}

repositories {
    mavenLocal()
}
```

## Development

### Building

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

### Publishing

```bash
./gradlew publishToMavenLocal
```

## License

This project is licensed under the MIT License.
