package com.hemad.stishield.model.riskassessment

data class RiskAssessmentProfile(
    var ageGroup: AgeGroup? = null,
    var sexAssignedAtBirth: SexAtBirth? = null,
    var sexuallyActive: Boolean? = null,
    var sexualRiskFactors: Boolean? = null,
    var analOrOralActs: Boolean? = null,
    var msm: Boolean? = null,
    var sharedNeedles: Boolean? = null,
    var pregnantOrPlanning: Boolean? = null,
    var vaccinatedForHepatitisA: Boolean? = null,
    var vaccinatedForHepatitisB: Boolean? = null,
    var vaccinatedForHPV: Boolean? = null
) {
    enum class AgeGroup {
        BELOW25,
        ABOVE25
    }

    enum class SexAtBirth {
        MALE,
        FEMALE
    }
}
