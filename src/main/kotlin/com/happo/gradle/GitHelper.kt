package com.happo.gradle

class GitHelper() {
    private val fallbackShasCount = 100

    fun findHEADSha(): String {
        return try {
            val process =
                    ProcessBuilder("git", "rev-parse", "HEAD").redirectErrorStream(true).start()

            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                output
            } else {
                throw RuntimeException(
                        "Failed to get HEAD SHA: git rev-parse HEAD exited with code $exitCode"
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to execute git rev-parse HEAD: ${e.message}", e)
        }
    }

    fun findFallbackShas(baselineSha: String): List<String> {
        return try {
            // log --format=%H --first-parent --max-count=${HAPPO_FALLBACK_SHAS_COUNT}
            // "$PREVIOUS_SHA"^
            val process =
                    ProcessBuilder(
                                    "git",
                                    "log",
                                    "--format=%H",
                                    "--first-parent",
                                    "--max-count=100",
                                    baselineSha
                            )
                            .redirectErrorStream(true)
                            .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                output.split("\n")
            } else {
                throw RuntimeException(
                        "Failed to get fallback SHA: git rev-parse HEAD~1 exited with code $exitCode"
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to execute git rev-parse HEAD~1: ${e.message}", e)
        }
    }

    fun findBaselineSha(): String {
        return try {
            val process =
                    ProcessBuilder("git", "merge-base", "main", "HEAD")
                            .redirectErrorStream(true)
                            .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                output
            } else {
                throw RuntimeException(
                        "Failed to get baseline SHA: git merge-base main HEAD exited with code $exitCode"
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to execute git merge-base main HEAD: ${e.message}", e)
        }
    }
}
