package com.happo.gradle

import kotlin.test.assertEquals
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
}
