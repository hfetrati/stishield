package com.hemad.stishield.model.riskassessment

internal class RiskAssessmentEngine(
    private val userProfile: RiskAssessmentProfile = RiskAssessmentProfile()
) {
    fun recordResponse(questionID: Int, response: String) {

        when (questionID) {

            1 -> userProfile.ageGroup =
                if (response == "18-24") RiskAssessmentProfile.AgeGroup.BELOW25 else RiskAssessmentProfile.AgeGroup.ABOVE25

            2 -> userProfile.sexAssignedAtBirth =
                if (response == "Male") RiskAssessmentProfile.SexAtBirth.MALE else RiskAssessmentProfile.SexAtBirth.FEMALE

            3 -> userProfile.sexuallyActive = if (response == "Yes") true else false
            4, 5, 6 -> {
                if ((userProfile.sexualRiskFactors == null) && (response == "Yes")) {
                    userProfile.sexualRiskFactors = true
                }
            }

            7 -> userProfile.analOrOralActs = if (response == "Yes") true else false
            8 -> userProfile.msm = if (response == "None of the Above") false else true
            9 -> userProfile.sharedNeedles = if (response == "Yes") true else false
            10 -> userProfile.pregnantOrPlanning = if (response == "Yes") true else false
            11 -> userProfile.vaccinatedForHepatitisA = if (response == "Yes") true else false
            12 -> userProfile.vaccinatedForHepatitisB = if (response == "Yes") true else false
            13 -> userProfile.vaccinatedForHPV = if (response == "Yes") true else false


        }

    }

    fun nextQuestionId(currentQuestionId: Int?): Int? = when (currentQuestionId) {
        0, 1, 2 -> currentQuestionId + 1
        3 -> if (userProfile.sexuallyActive == true) 4 else 9
        4, 5, 6 -> currentQuestionId + 1
        7 -> if (userProfile.sexAssignedAtBirth == RiskAssessmentProfile.SexAtBirth.MALE) 8 else 9
        8 -> 9
        9 -> if (userProfile.sexAssignedAtBirth == RiskAssessmentProfile.SexAtBirth.FEMALE) 10 else 11
        10, 11, 12 -> currentQuestionId + 1
        else -> null
    }

    fun generateVaccineRecommendation(): String {

        val recommendations = mutableListOf<String>()

        if (userProfile.vaccinatedForHepatitisA == false) {
            recommendations.add("Hepatitis A")
        }
        if (userProfile.vaccinatedForHepatitisB == false) {
            recommendations.add("Hepatitis B")
        }
        if (userProfile.vaccinatedForHPV ==  false) {
            recommendations.add("HPV")
        }

        if (recommendations.isNotEmpty()) {
            val vaccines = recommendations.joinToString(", ")
            return "\n• Discuss getting the $vaccines vaccine(s) with your healthcare provider."
        } else {
            return ""
        }
    }



    fun generateSTDTestRecommendations():String {

        val recommendations = mutableListOf<String>()

        if ((userProfile.sexAssignedAtBirth == RiskAssessmentProfile.SexAtBirth.FEMALE) && (userProfile.sexuallyActive == true) && (userProfile.ageGroup == RiskAssessmentProfile.AgeGroup.BELOW25)){
            recommendations.add("Get tested for gonorrhea and chlamydia every year.")
        }

        if ((userProfile.sexAssignedAtBirth == RiskAssessmentProfile.SexAtBirth.FEMALE)  && (userProfile.ageGroup == RiskAssessmentProfile.AgeGroup.ABOVE25) && (userProfile.sexualRiskFactors == true)){
            recommendations.add("Get tested for gonorrhea and chlamydia every year.")
        }

        if (userProfile.pregnantOrPlanning == true){
            recommendations.add("Get tested for syphilis, HIV, Hepatitis B, and Hepatitis C, chlamydia and gonorrhea early in pregnancy. Repeat testing may be needed in some cases.")
        }

        if (userProfile.msm == true){
            if (userProfile.sexualRiskFactors == true){
                recommendations.add("Get tested for HIV, syphilis, chlamydia, and gonorrhea frequently (e.g., every 3 to 6 months).")
            } else {
                recommendations.add("Get tested for HIV, syphilis, chlamydia, and gonorrhea at least once a year.")
            }
        }

        if ((userProfile.sharedNeedles ==  true) || (userProfile.sexualRiskFactors == true) ){

            if (!recommendations.any { it.contains("HIV") }) recommendations.add("Get tested for HIV at least once a year.")

        }

        if (userProfile.analOrOralActs == true){
            recommendations.add("Talk with a healthcare provider about throat and rectal testing options.")
        }

        val isHIVRecommended = recommendations.any { it.contains("HIV") }

        if ((recommendations.isEmpty()) || !isHIVRecommended) recommendations.add("Get tested for HIV at least once if you have not been tested before.")

        val recommendation = StringBuilder("")

        for ((index, str) in recommendations.withIndex()) {
            recommendation.append("• $str")
            if (index < recommendations.size - 1) {
                recommendation.append("\n")
            }
        }

        return recommendation.toString()


    }
}
