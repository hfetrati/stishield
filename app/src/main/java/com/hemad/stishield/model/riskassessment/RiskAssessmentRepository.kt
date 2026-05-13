package com.hemad.stishield.model.riskassessment

import android.content.Context
import com.hemad.stishield.R
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import com.hemad.stishield.model.common.UserDataRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.InputStream

class RiskAssessmentRepository(context: Context) {

    private val appContext = context
    private val userDataRepo = UserDataRepository(context)
    private var currentQuestionID: Int? = null
    private val userProfile = RiskAssessmentProfile()
    private val questions:List<RiskAssessmentQuestion> = readQuestionsFromRaw()


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

    fun createMessageFromQuestion(question:RiskAssessmentQuestion): ChatItem.MessageItem {
        return ChatItem.MessageItem(Message(
            body = question.text,
            role = Message.SenderRole.BOT,
            buttonTitles = question.responses,
            id = question.id
        ))
    }

    fun getFirstMessage(): ChatItem.MessageItem {
        currentQuestionID = 0
        return createMessageFromQuestion(questions[0])
    }

    suspend fun getNextMessage(): ChatItem? {

        return when (currentQuestionID) {

            0 -> {
                currentQuestionID = 1
                createMessageFromQuestion(questions[1])
            }

            1 -> {
                currentQuestionID = 2
                createMessageFromQuestion(questions[2])
            }

            2 -> {
                currentQuestionID = 3
                createMessageFromQuestion(questions[3])
            }

            3 -> if (userProfile.sexuallyActive == true) {
                currentQuestionID = 4
                createMessageFromQuestion(questions[4])
            } else {
                currentQuestionID = 9
                createMessageFromQuestion(questions[9])
            }

            4 -> {
                currentQuestionID = 5
                createMessageFromQuestion(questions[5])
            }

            5 -> {
                currentQuestionID = 6
                createMessageFromQuestion(questions[6])
            }

            6 -> {
                currentQuestionID = 7
                createMessageFromQuestion(questions[7])
            }

            7 -> if(userProfile.sexAssignedAtBirth == RiskAssessmentProfile.SexAtBirth.MALE){
                currentQuestionID = 8
                createMessageFromQuestion(questions[8])
            } else {
                currentQuestionID = 9
                createMessageFromQuestion(questions[9])
            }

            8 -> {
                currentQuestionID = 9
                createMessageFromQuestion(questions[9])
            }

            9 -> if (userProfile.sexAssignedAtBirth == RiskAssessmentProfile.SexAtBirth.FEMALE){
                currentQuestionID = 10
                createMessageFromQuestion(questions[10])
            } else {
                currentQuestionID = 11
                createMessageFromQuestion(questions[11])
            }

            10 -> {
                currentQuestionID = 11
                createMessageFromQuestion(questions[11])
            }

            11 -> {
                currentQuestionID = 12
                createMessageFromQuestion(questions[12])
            }

            12 -> {
                currentQuestionID = 13
                createMessageFromQuestion(questions[13])
            }

            13 -> {

                val (resultText,serverResult) = getFinalResult()
                ChatItem.MessageItemWithServerResult(Message(body = resultText, role = Message.SenderRole.BOT),serverResult)

            }

            else -> null

        }

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


    suspend fun adjustUserPoints():Pair<Boolean, Result<Unit>?> {

        if (!userDataRepo.didEarnRiskAssessmentPoints) {
            userDataRepo.points += 100
            userDataRepo.didEarnRiskAssessmentPoints = true
            val result = userDataRepo.updateUserPoints()
            return Pair(true,result)
        } else {
            return Pair(false,null)
        }
    }

    suspend fun getFinalResult():Pair<String,Result<Unit>?> {

        val recommendation = StringBuilder("Based on the Centers for Disease Control and Prevention (CDC) it is recommended that you:\n\n")
        val stdRecommendations = generateSTDTestRecommendations()
        val vaccineRecommendations = generateVaccineRecommendation()
        recommendation.append(stdRecommendations)
        recommendation.append(vaccineRecommendations)

        val (didUserEarnPoints,serverSyncResult) = adjustUserPoints()
        if (didUserEarnPoints){
            recommendation.append("\n\n You earned <b>100 ⭐</b>! Well done!")
        }

        return Pair(recommendation.toString(),serverSyncResult)

    }

    fun getNumberOfQuestions():Int {
        return questions.size
    }

    fun readQuestionsFromRaw(): List<RiskAssessmentQuestion> {

        val inputStream: InputStream = appContext.resources.openRawResource(R.raw.risk_assessment_questions)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        return Json.decodeFromString(jsonString)

    }


}