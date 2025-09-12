package com.happo.gradle

import com.fasterxml.jackson.databind.ObjectMapper

class GitHelper() {
    private val fallbackShasCount = 100

    fun findHEADSha(disableGitHubDetection: Boolean = false): String {
        // Check if we're in a GitHub pull request environment
        // First check environment variables (for real GitHub Actions)
        val githubEventName =
                System.getenv("GITHUB_EVENT_NAME") ?: System.getProperty("GITHUB_EVENT_NAME")
        val githubEventPath =
                System.getenv("GITHUB_EVENT_PATH") ?: System.getProperty("GITHUB_EVENT_PATH")

        if (!disableGitHubDetection && githubEventName == "pull_request" && githubEventPath != null
        ) {
            return try {
                // Read the GitHub event JSON to get the PR head SHA
                val eventJson = java.io.File(githubEventPath).readText()
                val objectMapper = ObjectMapper()
                val eventNode = objectMapper.readTree(eventJson)
                val headSha = eventNode.path("pull_request").path("head").path("sha").asText()
                headSha
            } catch (e: Exception) {
                // Fall back to git command if JSON parsing fails
                getHeadShaFromGit()
            }
        }

        // Fall back to git command for non-PR environments or if GitHub detection fails
        return getHeadShaFromGit()
    }

    private fun getHeadShaFromGit(): String {
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
                                    "--max-count=$fallbackShasCount",
                                    "$baselineSha^"
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

    fun findBaselineSha(fromSha: String = "HEAD", baseBranch: String = "origin/main"): String {
        return try {
            val process =
                    ProcessBuilder("git", "merge-base", baseBranch, fromSha)
                            .redirectErrorStream(true)
                            .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                output
            } else {
                throw RuntimeException(
                        "Failed to get baseline SHA: git merge-base $baseBranch $fromSha exited with code $exitCode"
                )
            }
        } catch (e: Exception) {
            throw RuntimeException(
                    "Failed to execute git merge-base $baseBranch $fromSha: ${e.message}",
                    e
            )
        }
    }

    fun getCommitSubject(sha: String = "HEAD"): String {
        return try {
            val process =
                    ProcessBuilder("git", "log", "--format=%s", "-n", "1", sha)
                            .redirectErrorStream(true)
                            .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                output
            } else {
                throw RuntimeException(
                        "Failed to get commit subject: git log --format=%s -n 1 $sha exited with code $exitCode"
                )
            }
        } catch (e: Exception) {
            throw RuntimeException(
                    "Failed to execute git log --format=%s -n 1 $sha: ${e.message}",
                    e
            )
        }
    }
}
