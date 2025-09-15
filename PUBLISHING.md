# Publishing Guide

This guide explains how to publish the Happo Gradle Plugin to the Gradle Plugin Portal.

## Prerequisites

1. **Gradle Plugin Portal Account**: Create an account at [plugins.gradle.org](https://plugins.gradle.org/)
2. **API Keys**: Generate API keys for publishing
3. **Domain Verification**: Ensure you have control over the `io.happo` domain (for the plugin ID)

## Setup Steps

The build script automatically loads properties from `~/.gradle/gradle.properties` if it exists.

### 1. Configure Gradle Plugin Portal Credentials

Create a `~/.gradle/gradle.properties` file (if it doesn't already exists) and
add your Gradle Plugin Portal credentials:

```properties
gradle.publish.key=your-publish-key
gradle.publish.secret=your-publish-secret
```

#### Getting Your API Keys

1. Go to [plugins.gradle.org](https://plugins.gradle.org/)
2. Log in to your account
3. Navigate to "API Keys" section
4. Create a new API key
5. Copy the publish key and secret to your `~/.gradle/gradle.properties` file

### 2. Update Version

Before publishing, update the version in `build.gradle.kts`:

```kotlin
version = "1.0.1" // or your desired version
```

## Publishing Commands

### Publish to Gradle Plugin Portal

```bash
./gradlew publishPlugins
```

This will:

- Build the project
- Upload the plugin to the Gradle Plugin Portal
- Make it available for users to apply with `plugins { id("io.happo.gradle") version "1.0.1" }`

### Dry Run (Test Before Publishing)

To see what would be published without actually publishing:

```bash
./gradlew publishPlugins --dry-run
```

## Plugin Configuration

The plugin is configured in `build.gradle.kts` with the following details:

- **Plugin ID**: `io.happo.gradle`
- **Display Name**: "Happo Gradle Plugin"
- **Description**: "A Gradle plugin for uploading and comparing Happo visual regression test reports"

## Verification

After publishing, your plugin will be available at:

- **Plugin Portal**: [https://plugins.gradle.org/plugin/io.happo.gradle](https://plugins.gradle.org/plugin/io.happo.gradle)
- **Usage**: Users can apply it with `plugins { id("io.happo.gradle") version "1.0.1" }`

## Troubleshooting

### Common Issues

1. **Authentication Failed**: Double-check your Gradle Plugin Portal credentials
2. **Plugin ID Conflict**: Ensure the `io.happo.gradle` plugin ID is available
3. **Version Already Exists**: Increment the version number if the version already exists
4. **Domain Verification**: Ensure you have access to the `io.happo` domain

### Useful Commands

- Check what will be published: `./gradlew publishPlugins --dry-run`
- Validate plugin metadata: `./gradlew validatePlugins`
- View plugin details: `./gradlew pluginUnderTestMetadata`

## Version Management

Follow semantic versioning:

- **Patch** (1.0.1): Bug fixes
- **Minor** (1.1.0): New features, backward compatible
- **Major** (2.0.0): Breaking changes

## Security Notes

- Never commit credentials to git
- Use environment variables or local gradle.properties
- Keep your API keys secure
- Regularly rotate API keys

## Benefits of Gradle Plugin Portal

- **Official Distribution**: The official way to distribute Gradle plugins
- **Easy Discovery**: Users can find your plugin easily
- **Automatic Updates**: Users get notified of new versions
- **No Manual Setup**: No need to configure repositories
- **Better Integration**: Works seamlessly with Gradle's plugin system
