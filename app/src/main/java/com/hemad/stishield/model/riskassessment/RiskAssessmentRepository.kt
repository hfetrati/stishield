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
    private val assessment = RiskAssessmentEngine()
    private val questions:List<RiskAssessmentQuestion> = readQuestionsFromRaw()


    fun recordResponse(questionID: Int, response: String) {
        assessment.recordResponse(questionID, response)
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
        if (currentQuestionID == 13) {
            val (resultText, serverResult) = getFinalResult()
            return ChatItem.MessageItemWithServerResult(
                Message(body = resultText, role = Message.SenderRole.BOT), serverResult
            )
        }

        val nextId = assessment.nextQuestionId(currentQuestionID) ?: return null
        currentQuestionID = nextId
        return createMessageFromQuestion(questions[nextId])
    }

    fun generateVaccineRecommendation(): String = assessment.generateVaccineRecommendation()

    fun generateSTDTestRecommendations(): String = assessment.generateSTDTestRecommendations()

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