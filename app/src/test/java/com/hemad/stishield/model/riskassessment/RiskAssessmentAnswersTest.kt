@file:Suppress("IllegalIdentifier")

package com.hemad.stishield.model.riskassessment

import org.junit.Assert.*
import org.junit.Test

// Checks that risk assessment answers update the intended profile fields and recommendation behavior.
class RiskAssessmentAnswersTest {
    private val profile = RiskAssessmentProfile()
    private val assessment = RiskAssessmentEngine(profile)

    @Test
    fun `age choices map to the intended groups`() {
        assessment.recordResponse(1, "18-24")
        assertEquals(RiskAssessmentProfile.AgeGroup.BELOW25, profile.ageGroup)
        assessment.recordResponse(1, "25+")
        assertEquals(RiskAssessmentProfile.AgeGroup.ABOVE25, profile.ageGroup)
    }

    @Test
    fun `sex choices map to the intended groups`() {
        assessment.recordResponse(2, "Male")
        assertEquals(RiskAssessmentProfile.SexAtBirth.MALE, profile.sexAssignedAtBirth)
        assessment.recordResponse(2, "Female")
        assertEquals(RiskAssessmentProfile.SexAtBirth.FEMALE, profile.sexAssignedAtBirth)
    }

    @Test
    fun `yes and no update each independent boolean answer`() {
        val fields: Map<Int, () -> Boolean?> = mapOf(
            3 to { profile.sexuallyActive },
            7 to { profile.analOrOralActs },
            9 to { profile.sharedNeedles },
            10 to { profile.pregnantOrPlanning },
            11 to { profile.vaccinatedForHepatitisA },
            12 to { profile.vaccinatedForHepatitisB },
            13 to { profile.vaccinatedForHPV }
        )
        fields.forEach { (questionId, answer) ->
            assertNull("Unanswered question $questionId", answer())
            assessment.recordResponse(questionId, "Yes")
            assertEquals("Yes for question $questionId", true, answer())
            assessment.recordResponse(questionId, "No")
            assertEquals("No for question $questionId", false, answer())
        }
    }

    @Test
    fun `any positive sexual risk answer survives later negative answers`() {
        for (positiveQuestion in 4..6) {
            val scenario = RiskAssessmentProfile()
            val engine = RiskAssessmentEngine(scenario)
            for (questionId in 4..6) {
                engine.recordResponse(questionId, if (questionId == positiveQuestion) "Yes" else "No")
            }
            assertEquals("Positive answer to question $positiveQuestion", true, scenario.sexualRiskFactors)
        }
    }

    @Test
    fun `all negative sexual risk answers do not trigger the annual HIV rule`() {
        (4..6).forEach { assessment.recordResponse(it, "No") }
        assertFalse(
            "Negative risk answers should not trigger annual HIV testing",
            assessment.generateSTDTestRecommendations()
                .contains("Get tested for HIV at least once a year.")
        )
    }

    @Test
    fun `each MSM choice is distinguished from none of the above`() {
        listOf("Gay", "Bisexual", "Other men who have sex with men").forEach { answer ->
            assessment.recordResponse(8, answer)
            assertEquals(answer, true, profile.msm)
        }
        assessment.recordResponse(8, "None of the Above")
        assertEquals(false, profile.msm)
    }

    @Test
    fun `unsure vaccine answers retain the existing vaccine discussion behavior`() {
        (11..13).forEach { assessment.recordResponse(it, "Unsure") }
        assertEquals(
            "\n• Discuss getting the Hepatitis A, Hepatitis B, HPV vaccine(s) with your healthcare provider.",
            assessment.generateVaccineRecommendation()
        )
    }
}
