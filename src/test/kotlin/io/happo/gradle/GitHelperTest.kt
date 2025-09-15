package io.happo.gradle

import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

class GitHelperTest {

    @Test
    fun `should get HEAD SHA`() {
        val gitHelper = GitHelper()
        val sha = gitHelper.findHEADSha()

        assertNotNull(sha)
        assertEquals(40, sha.length) // SHA should be 40 characters long
    }

    @Test
    fun `should get HEAD SHA from GitHub event`() {
        val gitHelper = GitHelper()
        val realHEAD = gitHelper.findHEADSha()
        val sha =
                gitHelper.findHEADSha(
                        githubEventName = "pull_request",
                        githubEventPath = "src/test/resources/github-event.json"
                )

        assertNotNull(sha)
        assertEquals(40, sha.length) // SHA should be 40 characters long
        assertNotEquals(realHEAD, sha)
    }

    @Test
    fun `should get commit subject`() {
        val gitHelper = GitHelper()
        val subject = gitHelper.getCommitSubject()

        assertNotNull(subject)
        assert(subject.isNotEmpty())
    }

    @Test
    fun `should get commit subject for specific SHA`() {
        val gitHelper = GitHelper()
        val headSha = gitHelper.findHEADSha()
        val subject = gitHelper.getCommitSubject(headSha)

        assertNotNull(subject)
        assert(subject.isNotEmpty())
    }

    @Test
    fun `should get baseline SHA with default main branch`() {
        val gitHelper = GitHelper()
        val baselineSha = gitHelper.findBaselineSha()

        assertNotNull(baselineSha)
        assertEquals(40, baselineSha.length) // SHA should be 40 characters long
    }

    @Test
    fun `should get baseline SHA with custom base branch`() {
        val gitHelper = GitHelper()
        val baselineSha = gitHelper.findBaselineSha(baseBranch = "origin/main")

        assertNotNull(baselineSha)
        assertEquals(40, baselineSha.length) // SHA should be 40 characters long
    }

    @Test
    fun `should get commit link`() {
        val gitHelper = GitHelper()
        val link = gitHelper.getCommitLink(githubEventPath = "src/test/resources/github-event.json")
        assertNotNull(link)
    }
}
