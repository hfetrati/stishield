@file:Suppress("IllegalIdentifier")

package com.hemad.stishield.model.riskassessment

import org.junit.Assert.*
import org.junit.Test

// Checks that each risk assessment path visits the expected questions and finishes without looping.
class RiskAssessmentFlowTest {
    @Test
    fun `sexually active male receives MSM question and skips pregnancy question`() {
        assertEquals(
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13),
            questionPath("Male", "Yes")
        )
    }

    @Test
    fun `sexually active female skips MSM question and receives pregnancy question`() {
        assertEquals(
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13),
            questionPath("Female", "Yes")
        )
    }

    @Test
    fun `sexually inactive male skips sexual activity details and pregnancy question`() {
        assertEquals(listOf(0, 1, 2, 3, 9, 11, 12, 13), questionPath("Male", "No"))
    }

    @Test
    fun `sexually inactive female skips sexual activity details but receives pregnancy question`() {
        assertEquals(listOf(0, 1, 2, 3, 9, 10, 11, 12, 13), questionPath("Female", "No"))
    }

    @Test
    fun `navigation has no next question before starting or after the final question`() {
        val assessment = RiskAssessmentEngine()
        assertNull(assessment.nextQuestionId(null))
        assertNull(assessment.nextQuestionId(13))
    }

    private fun questionPath(sex: String, sexuallyActive: String): List<Int> {
        val assessment = RiskAssessmentEngine()
        val answers = mapOf(1 to "25+", 2 to sex, 3 to sexuallyActive, 8 to "None of the Above")
        val path = mutableListOf<Int>()
        var currentId: Int? = 0
        while (currentId != null) {
            assertTrue("Assessment must finish without looping", path.size < 14)
            val id = currentId
            path.add(id)
            assessment.recordResponse(id, answers[id] ?: "No")
            currentId = assessment.nextQuestionId(id)
        }
        return path
    }
}
