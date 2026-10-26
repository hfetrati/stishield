@file:Suppress("IllegalIdentifier")

package com.hemad.stishield.model.riskassessment

import org.junit.Assert.*
import org.junit.Test

// Checks that risk assessment profile entries produce the expected testing and vaccine recommendations under the app's existing rules.
class RiskAssessmentRecommendationsTest {
    private val profile = RiskAssessmentProfile()
    private val assessment = RiskAssessmentEngine(profile)
    private val annualScreening = "Get tested for gonorrhea and chlamydia every year."
    private val baselineHiv = "Get tested for HIV at least once if you have not been tested before."
    private val annualHiv = "Get tested for HIV at least once a year."

    @Test
    fun `unanswered profile gets baseline HIV text but no vaccine assumptions`() {
        assertEquals("• $baselineHiv", assessment.generateSTDTestRecommendations())
        assertEquals("", assessment.generateVaccineRecommendation())
    }

    @Test
    fun `sexually active female under 25 receives annual screening and baseline HIV text`() {
        profile.sexAssignedAtBirth = RiskAssessmentProfile.SexAtBirth.FEMALE
        profile.ageGroup = RiskAssessmentProfile.AgeGroup.BELOW25
        profile.sexuallyActive = true
        assertEquals("• $annualScreening\n• $baselineHiv", assessment.generateSTDTestRecommendations())
    }

    @Test
    fun `female under 25 without confirmed sexual activity does not receive annual screening`() {
        profile.sexAssignedAtBirth = RiskAssessmentProfile.SexAtBirth.FEMALE
        profile.ageGroup = RiskAssessmentProfile.AgeGroup.BELOW25
        for (active in listOf(null, false)) {
            profile.sexuallyActive = active
            assertFalse(assessment.generateSTDTestRecommendations().contains(annualScreening))
        }
    }

    @Test
    fun `female 25 or older with sexual risk factors receives annual screening and HIV text`() {
        profile.sexAssignedAtBirth = RiskAssessmentProfile.SexAtBirth.FEMALE
        profile.ageGroup = RiskAssessmentProfile.AgeGroup.ABOVE25
        profile.sexualRiskFactors = true
        assertEquals("• $annualScreening\n• $annualHiv", assessment.generateSTDTestRecommendations())
    }

    @Test
    fun `female 25 or older without confirmed risk factors does not receive annual screening`() {
        profile.sexAssignedAtBirth = RiskAssessmentProfile.SexAtBirth.FEMALE
        profile.ageGroup = RiskAssessmentProfile.AgeGroup.ABOVE25
        for (risk in listOf(null, false)) {
            profile.sexualRiskFactors = risk
            assertFalse(assessment.generateSTDTestRecommendations().contains(annualScreening))
        }
    }

    @Test
    fun `pregnancy recommendation includes prenatal screening without duplicate HIV text`() {
        profile.pregnantOrPlanning = true
        profile.sharedNeedles = true
        val result = assessment.generateSTDTestRecommendations()
        assertEquals(
            "• Get tested for syphilis, HIV, Hepatitis B, and Hepatitis C, chlamydia and gonorrhea early in pregnancy. Repeat testing may be needed in some cases.",
            result
        )
    }

    @Test
    fun `MSM without additional risk receives annual screening without baseline HIV text`() {
        profile.msm = true
        assertEquals(
            "• Get tested for HIV, syphilis, chlamydia, and gonorrhea at least once a year.",
            assessment.generateSTDTestRecommendations()
        )
    }

    @Test
    fun `MSM with additional risk receives frequent screening without duplicate annual HIV text`() {
        profile.msm = true
        profile.sexualRiskFactors = true
        profile.sharedNeedles = true
        assertEquals(
            "• Get tested for HIV, syphilis, chlamydia, and gonorrhea frequently (e.g., every 3 to 6 months).",
            assessment.generateSTDTestRecommendations()
        )
    }

    @Test
    fun `shared needles triggers annual HIV text`() {
        profile.sharedNeedles = true
        assertEquals("• $annualHiv", assessment.generateSTDTestRecommendations())
    }

    @Test
    fun `oral or anal acts adds site testing text and keeps baseline HIV text`() {
        profile.analOrOralActs = true
        assertEquals(
            "• Talk with a healthcare provider about throat and rectal testing options.\n• $baselineHiv",
            assessment.generateSTDTestRecommendations()
        )
    }

    @Test
    fun `vaccines include only explicitly unvaccinated entries`() {
        profile.vaccinatedForHepatitisA = false
        profile.vaccinatedForHepatitisB = true
        profile.vaccinatedForHPV = null
        assertEquals(
            "\n• Discuss getting the Hepatitis A vaccine(s) with your healthcare provider.",
            assessment.generateVaccineRecommendation()
        )
    }

    @Test
    fun `all missing vaccines appear once in the expected order`() {
        profile.vaccinatedForHepatitisA = false
        profile.vaccinatedForHepatitisB = false
        profile.vaccinatedForHPV = false
        assertEquals(
            "\n• Discuss getting the Hepatitis A, Hepatitis B, HPV vaccine(s) with your healthcare provider.",
            assessment.generateVaccineRecommendation()
        )
    }

    @Test
    fun `fully vaccinated profile receives no vaccine text`() {
        profile.vaccinatedForHepatitisA = true
        profile.vaccinatedForHepatitisB = true
        profile.vaccinatedForHPV = true
        assertEquals("", assessment.generateVaccineRecommendation())
    }
}
