package com.hemad.stishield.model.quiz

import android.content.Context
import com.hemad.stishield.R
import com.hemad.stishield.model.common.UserDataRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.InputStream
import kotlin.math.floor

class QuizRepository(context: Context,difficulty:String) {

    val appContext = context
    val questions:List<QuizQuestion> = readQuestionsFromRaw(difficulty)
    val userRepo = UserDataRepository(context)
    var currentQuestionIndex:Int = -1
    var correctlyAnsweredNumb:Int = 0
    var totalQuestionsNum = questions.size
    val difficulty = difficulty

    fun readQuestionsFromRaw(difficulty: String): List<QuizQuestion> {

        val resourceMap = mapOf(
            "easy" to R.raw.quiz_questions_easy,
            "medium" to R.raw.quiz_questions_medium,
            "hard" to R.raw.quiz_questions_hard
        )
        val resourceId = resourceMap[difficulty]
        val inputStream: InputStream = appContext.resources.openRawResource(resourceId!!)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        return Json.decodeFromString(jsonString)

    }

    fun getNextQuestion(): QuizQuestion {
        currentQuestionIndex += 1
        return questions[currentQuestionIndex]
    }

    fun recordUserResponse(response:String){

        val correctChoiceIndex = questions[currentQuestionIndex].correctChoiceIndex
        if (correctChoiceIndex != null){
            val correctResponse = questions[currentQuestionIndex].choices[correctChoiceIndex]
            if (correctResponse == response) {
                correctlyAnsweredNumb += 1
            }
        }
    }

    fun resetRepo() {

        correctlyAnsweredNumb = 0
        currentQuestionIndex = -1
    }


    suspend fun adjustUserPoints(difficulty: String):Pair<Boolean, Result<Unit>?> {

        if (difficulty == "easy") {
            if (!userRepo.didEarnQuizPoints_easy) {
                userRepo.points += 100
                userRepo.didEarnQuizPoints_easy = true
                val result = userRepo.updateUserPoints()
                return Pair(true,result)
            } else {
                return Pair(false,null)
            }
        } else if (difficulty == "medium"){
            if (!userRepo.didEarnQuizPoints_medium) {
                userRepo.points += 100
                userRepo.didEarnQuizPoints_medium = true
                val result = userRepo.updateUserPoints()
                return Pair(true,result)
            } else {
                return Pair(false,null)
            }
        } else {
            if (!userRepo.didEarnQuizPoints_hard) {
                userRepo.points += 100
                userRepo.didEarnQuizPoints_hard = true
                val result = userRepo.updateUserPoints()
                return Pair(true,result)
            } else {
                return Pair(false,null)
            }
        }
    }

    fun adjustTopScore(difficulty: String){

        val currentScore = floor((correctlyAnsweredNumb.toDouble()/totalQuestionsNum.toDouble())*100).toInt()
        if (difficulty == "easy") {
           if (userRepo.highestQuizScore_easy < currentScore){
               userRepo.highestQuizScore_easy = currentScore
           }
        } else if (difficulty == "medium"){
            if (userRepo.highestQuizScore_medium < currentScore){
                userRepo.highestQuizScore_medium = currentScore
            }
        } else {
            if (userRepo.highestQuizScore_hard < currentScore){
                userRepo.highestQuizScore_hard = currentScore
            }
        }
    }

    suspend fun getResult():Triple<String,Result<Unit>?,Result<Unit>>{

        val currentScore = floor((correctlyAnsweredNumb.toDouble()/totalQuestionsNum.toDouble())*100).toInt()
        var txt = ""
        var syncPointsWithServerRes:Result<Unit>? = null
        adjustTopScore(difficulty)
        val saveQuizScoreToServerRes = userRepo.recordQuizScore(difficulty,currentScore)
        if (currentScore == 100) {
            val (didUserEarnPoints,serverResult) = adjustUserPoints(difficulty)
            syncPointsWithServerRes = serverResult
            if (didUserEarnPoints) {
                txt = "Awesome! You nailed every question! You have earned <b>100 ⭐</b>."
            } else {
                txt = "Good job! You answered all questions correctly!"
            }
        } else {
            txt = "Good effort! You answered <b>$correctlyAnsweredNumb</b> out of <b>$totalQuestionsNum</b> (<b>$currentScore%</b>) questions correctly. Try again to improve your score!"
        }
        return Triple(txt,syncPointsWithServerRes, saveQuizScoreToServerRes)
    }

}