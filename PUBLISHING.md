# Publishing Guide

This guide explains how to publish the Happo Gradle Plugin to Maven Central.

## Prerequisites

1. **Sonatype Account**: Create an account at [Sonatype](https://central.sonatype.com/)
2. **GPG Key**: Generate a GPG key for signing artifacts
3. **Domain Verification**: Ensure you have control over the `io.happo` domain
4. **License**: The project uses MIT License (already configured)

## Setup Steps

### 1. Configure Sonatype Credentials

Add your Sonatype credentials to your local `gradle.properties` file (not committed to git):

```properties
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password
```

### 2. Configure GPG Signing

Add your GPG configuration to your local `gradle.properties`:

```properties
signing.keyId=your-gpg-key-id
signing.password=your-gpg-password
signing.secretKeyRingFile=/path/to/your/secring.gpg
```

Alternatively, you can use environment variables:

```bash
export SIGNING_KEY_ID=your-gpg-key-id
export SIGNING_PASSWORD=your-gpg-password
export SIGNING_SECRET_KEY_RING_FILE=/path/to/your/secring.gpg
```

### 3. Update Version

Before publishing, update the version in `build.gradle.kts`:

```kotlin
version = "1.0.1" // or your desired version
```

## Publishing Commands

### Publish to Staging Repository

```bash
./gradlew publishToSonatype
```

This will:

- Build the project
- Sign the artifacts with GPG
- Upload to Sonatype staging repository

### Release from Staging

After successful upload, you need to:

1. Go to [Sonatype Nexus](https://s01.oss.sonatype.org/)
2. Log in with your credentials
3. Navigate to "Staging Repositories"
4. Find your uploaded repository
5. Click "Close" to validate the artifacts
6. Once validation passes, click "Release" to publish to Maven Central

### Alternative: Auto-release

You can also use the `closeAndReleaseSonatypeStagingRepository` task to automatically close and release:

```bash
./gradlew closeAndReleaseSonatypeStagingRepository
```

## Verification

After release, your artifact will be available at:

- Maven Central: `https://repo1.maven.org/maven2/io/happo/gradle-happo/`
- Search: [Maven Central Search](https://search.maven.org/)

## Troubleshooting

### Common Issues

1. **Authentication Failed**: Double-check your Sonatype credentials
2. **Signing Failed**: Verify your GPG key configuration
3. **Validation Errors**: Check that all POM metadata is correct
4. **Domain Verification**: Ensure you have access to the `io.happo` domain

### Useful Commands

- Check what will be published: `./gradlew publishToSonatype --dry-run`
- Validate POM: `./gradlew generatePomFileForMavenPublication`
- View generated POM: `cat build/publications/maven/pom-default.xml`

## Version Management

Follow semantic versioning:

- **Patch** (1.0.1): Bug fixes
- **Minor** (1.1.0): New features, backward compatible
- **Major** (2.0.0): Breaking changes

## Security Notes

- Never commit credentials to git
- Use environment variables or local gradle.properties
- Keep your GPG key secure
- Regularly rotate passwords
