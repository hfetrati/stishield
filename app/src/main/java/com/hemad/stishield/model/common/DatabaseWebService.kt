package com.hemad.stishield.model.common

import android.util.Log
import com.hemad.stishield.BuildConfig
import com.hemad.stishield.model.leaderboard.LeaderBoardData
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONArray
import org.json.JSONObject

object DatabaseWebService {

    private val BACKEND_BASE_URL = BuildConfig.AWS_URL.trimEnd('/')

    private val REGISTER_USER_URL = "$BACKEND_BASE_URL/users/register"
    private val SYNC_POINTS_URL = "$BACKEND_BASE_URL/users/points"
    private val LEADERBOARD_URL = "$BACKEND_BASE_URL/leaderboard"
    private val RECORD_QUIZ_SCORE_URL = "$BACKEND_BASE_URL/users/quiz-score"
    private val SYNC_USAGE_TIME_URL = "$BACKEND_BASE_URL/users/usage-time"

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }

    suspend fun registerNewUser(email: String, nickname: String): Result<Unit> {
        return try {
            val requestBody = JSONObject().apply {
                put("email", email)
                put("nickname", nickname)
            }.toString()

            postRequest(REGISTER_USER_URL, requestBody)

            Result.success(Unit)

        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Unable to register new user. Check your internet connection and try again."))
        } catch (e: Exception) {
            Log.e("DatabaseWebService", "registerNewUser failed", e)
            Result.failure(e)
        }
    }

    suspend fun syncUserPoints(email: String, points: Int): Result<Unit> {
        return try {
            val requestBody = JSONObject().apply {
                put("email", email)
                put("points", points)
            }.toString()

            postRequest(SYNC_POINTS_URL, requestBody)

            Result.success(Unit)

        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Points were updated locally, but were not synced with the server. Check your internet connection."))
        } catch (e: Exception) {
            Log.e("DatabaseWebService", "syncUserPoints failed", e)
            Result.failure(e)
        }
    }

    suspend fun getLeaderboard(): Result<List<LeaderBoardData>> {
        return try {
            val responseText = getRequest(LEADERBOARD_URL)
            val jsonArray = JSONArray(responseText)

            val leaderboard = mutableListOf<LeaderBoardData>()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                val rank = item.optInt("rank", i + 1)
                val nickname = item.optString("nickname", "")
                val points = item.optInt("points", 0)
                val email = item.optString("email", "")

                if (points > 0) {
                    leaderboard.add(
                        LeaderBoardData(
                            rank = rank,
                            nickname = nickname,
                            points = points,
                            email = email
                        )
                    )
                }
            }

            Result.success(leaderboard)

        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Unable to fetch leaderboard data from server. Check your internet connection and try again."))
        } catch (e: Exception) {
            Log.e("DatabaseWebService", "getLeaderboard failed", e)
            Result.failure(e)
        }
    }

    suspend fun recordQuizScore(email: String, difficulty: String, score: Int): Result<Unit> {
        return try {
            val requestBody = JSONObject().apply {
                put("email", email)
                put("difficulty", difficulty)
                put("score", score)
            }.toString()

            postRequest(RECORD_QUIZ_SCORE_URL, requestBody)

            Result.success(Unit)

        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("Unable to record quiz score. Check your internet connection."))
        } catch (e: Exception) {
            Log.e("DatabaseWebService", "recordQuizScore failed", e)
            Result.failure(e)
        }
    }

    suspend fun syncUsageTime(email: String, usageTime: Long) {
        try {
            val requestBody = JSONObject().apply {
                put("email", email)
                put("usageTime", usageTime)
            }.toString()

            postRequest(SYNC_USAGE_TIME_URL, requestBody)

            Log.d("Usage Tracker", "Successfully saved usage time and timestamp to AWS.")

        } catch (e: TimeoutCancellationException) {
            Log.d("Usage Tracker", "Timeout while trying to save usage time to AWS.")
        } catch (e: Exception) {
            Log.d("Usage Tracker", "Failed to save usage time to cloud: ${e.message}")
        }
    }

    private suspend fun postRequest(url: String, requestBody: String): String {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        val responseText = response.bodyAsText()

        if (response.status.value !in 200..299) {
            Log.e("DatabaseWebService", "Backend error: ${response.status.value} $responseText")
            throw Exception("AWS request failed: ${response.status.value} $responseText")
        }

        return responseText
    }

    private suspend fun getRequest(url: String): String {
        val response = client.get(url)

        val responseText = response.bodyAsText()

        if (response.status.value !in 200..299) {
            Log.e("DatabaseWebService", "Backend error: ${response.status.value} $responseText")
            throw Exception("AWS request failed: ${response.status.value} $responseText")
        }

        return responseText
    }

}