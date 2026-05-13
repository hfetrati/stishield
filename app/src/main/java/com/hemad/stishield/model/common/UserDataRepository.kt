package com.hemad.stishield.model.common

import android.content.Context
import android.content.SharedPreferences
import com.hemad.stishield.model.leaderboard.LeaderBoardData

class UserDataRepository(context: Context){

    val cloud_db = DatabaseWebService
    val currentcontext = context

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("STIAppPrefs", Context.MODE_PRIVATE)

    var gameLevelIndex: Int
        get() = sharedPreferences.getInt("currentLevelIndex", 0)
        set(value) = sharedPreferences.edit().putInt("currentLevelIndex", value).apply()

    var gameTaskIndex: Int
        get() = sharedPreferences.getInt("currentTaskIndex", 0)
        set(value) = sharedPreferences.edit().putInt("currentTaskIndex", value).apply()

    var points: Int
        get() = sharedPreferences.getInt("currentPoints", 0)
        set(value) = sharedPreferences.edit().putInt("currentPoints", value).apply()

    var gameFactIndex: Int
        get() = sharedPreferences.getInt("currentFactIndex", 0)
        set(value) = sharedPreferences.edit().putInt("currentFactIndex", value).apply()

    var email: String?
        get() = sharedPreferences.getString("email", null)
        set(value) = sharedPreferences.edit().putString("email", value).apply()

    var nickname: String?
        get() = sharedPreferences.getString("nickname", null)
        set(value) = sharedPreferences.edit().putString("nickname", value).apply()

    var didEarnQuizPoints_easy: Boolean
        get() = sharedPreferences.getBoolean("didEarnQuizPoints_easy", false)
        set(value) = sharedPreferences.edit().putBoolean("didEarnQuizPoints_easy", value).apply()

    var didEarnQuizPoints_medium: Boolean
        get() = sharedPreferences.getBoolean("didEarnQuizPoints_medium", false)
        set(value) = sharedPreferences.edit().putBoolean("didEarnQuizPoints_medium", value).apply()

    var didEarnQuizPoints_hard: Boolean
        get() = sharedPreferences.getBoolean("didEarnQuizPoints_hard", false)
        set(value) = sharedPreferences.edit().putBoolean("didEarnQuizPoints_hard", value).apply()

    var highestQuizScore_easy: Int
        get() = sharedPreferences.getInt("highestQuizScore_easy", 0)
        set(value) = sharedPreferences.edit().putInt("highestQuizScore_easy", value).apply()

    var highestQuizScore_medium: Int
        get() = sharedPreferences.getInt("highestQuizScore_medium", 0)
        set(value) = sharedPreferences.edit().putInt("highestQuizScore_medium", value).apply()

    var highestQuizScore_hard: Int
        get() = sharedPreferences.getInt("highestQuizScore_hard", 0)
        set(value) = sharedPreferences.edit().putInt("highestQuizScore_hard", value).apply()

    var didEarnRiskAssessmentPoints: Boolean
        get() = sharedPreferences.getBoolean("didEarnRiskAssessmentPoints", false)
        set(value) = sharedPreferences.edit().putBoolean("didEarnRiskAssessmentPoints", value).apply()
    var didFinishOnboarding: Boolean
        get() = sharedPreferences.getBoolean("didFinishOnboarding", false)
        set(value) = sharedPreferences.edit().putBoolean("didFinishOnboarding", value).apply()
    var isRequestNotificationPermissionDone: Boolean
        get() = sharedPreferences.getBoolean("isRequestNotificationPermissionDone", false)
        set(value) = sharedPreferences.edit().putBoolean("isRequestNotificationPermissionDone", value).apply()
    var usageTime: Long
        get() = sharedPreferences.getLong("totalUsageTime", 0)
        set(value) = sharedPreferences.edit().putLong("totalUsageTime", value).apply()



    suspend fun registerNewUser(newEmail: String, newNickname: String):Result<Unit> {

        return try {
            val result = cloud_db.registerNewUser(newEmail, newNickname)
            result.onSuccess {
                email = newEmail
                nickname = newNickname
            }
            result
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun updateUserPoints():Result<Unit> {

        return try {
            val result = cloud_db.syncUserPoints(email!!, points)
            result
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    suspend fun getLeaderboardData(): Result<List<LeaderBoardData>> {

        return try {
            val result = cloud_db.getLeaderboard()
            result
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun recordQuizScore(quizDifficulty:String,quizScore:Int):Result<Unit> {
        return try {
            val result = cloud_db.recordQuizScore(email!!, quizDifficulty,quizScore)
            result
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun updateUsageTime() {
        cloud_db.syncUsageTime(email!!, usageTime)
    }

}