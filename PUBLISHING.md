# Publishing Guide

This guide explains how to publish the Happo Gradle Plugin to Maven Central.

## Prerequisites

1. **Sonatype Account**: Create an account at [Sonatype](https://central.sonatype.com/)
2. **GPG Key**: Generate a GPG key for signing artifacts
3. **Domain Verification**: Ensure you have control over the `io.happo` domain
4. **License**: The project uses MIT License (already configured)

## Setup Steps

The build script automatically loads properties from `gradle-local.properties` if it exists. This file is gitignored to keep your credentials secure.

### 1. Configure Sonatype Credentials

Create a `gradle-local.properties` file and add your Sonatype credentials:

```properties
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password
```

### 2. Configure GPG Signing

Configure `gradle-local.properties` with the following properties. To generate a
new GPG signing key, run `gpg --full-generate-key`, then add the properties.

```properties
signing.keyId=your-gpg-key-id
signing.password=your-gpg-passphrase
signing.secretKeyRingFile=/Users/yourusername/.gnupg/secring.gpg
```

#### Finding Your GPG Key ID

1. List your GPG keys:

   ```bash
   gpg --list-secret-keys --keyid-format LONG
   ```

2. Look for output like:

   ```
   sec   rsa4096/ABC123DEF4567890 2023-01-01 [SC] [expires: 2025-01-01]
   ```

   The key ID is `ABC123DEF4567890` (the part after the slash).

3. Export your public key to add to Sonatype:
   ```bash
   gpg --armor --export ABC123DEF4567890 | pbcopy
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
