package com.hemad.stishield.model.common

import android.content.Context
import android.util.Log
import com.hemad.stishield.model.leaderboard.LeaderBoardData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FirebaseWebService {

    private val db = FirebaseFirestore.getInstance()

    suspend fun registerNewUser(email: String, nickname: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(15000L) {

                    val dateFormatter = SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss", Locale.getDefault())
                    dateFormatter.timeZone = TimeZone.getTimeZone("America/Halifax")
                    val currentDateTime = dateFormatter.format(Date())

                    val user = hashMapOf(
                        "email" to email,
                        "nickname" to nickname,
                        "registrationDateTime" to currentDateTime
                    )

                    db.collection("users")
                        .document(email)
                        .set(user)
                        .await()
                    Result.success(Unit)
                }
            } catch (e: TimeoutCancellationException) {
                Result.failure(Exception("Unable to register new user. Check your internet connection and try again."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncUserPoints(email: String, points: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(15000L) {
                    db.collection("users")
                        .document(email)
                        .update("points", points)
                        .await()
                    Result.success(Unit)
                }
            } catch (e: TimeoutCancellationException) {
                Result.failure(Exception("Points were updated locally, but were not synced with the server. Check your internet connection."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun getLeaderboard(): Result<List<LeaderBoardData>> {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(15000L) {
                    val result = db.collection("users")
                        .orderBy("points", Query.Direction.DESCENDING)
                        .get()
                        .await()

                    val leaderboard = result.documents.mapNotNull { document ->
                        val nickname = document.getString("nickname") ?: ""
                        val points = document.getLong("points")?.toInt() ?: 0
                        val email = document.getString("email") ?: ""
                        if (points > 0) Triple(nickname, points, email) else null
                    }

                    val rankedLeaderboard = leaderboard
                        .sortedByDescending { it.second }
                        .foldIndexed(mutableListOf<LeaderBoardData>()) { index, acc, (nickname, points, email) ->
                            val rank = if (index > 0 && leaderboard[index - 1].second == points) {
                                acc.last().rank
                            } else {
                                index + 1
                            }
                            acc.add(LeaderBoardData(rank, nickname, points, email))
                            acc
                        }

                    Result.success(rankedLeaderboard)
                }
            } catch (e: TimeoutCancellationException) {
                Result.failure(Exception("Unable to fetch leaderboard data from server. Check your internet connection and try again."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun recordQuizScore(email: String, difficulty: String, score: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                withTimeout(15000L) {
                    val dateFormatter = SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss", Locale.getDefault())
                    dateFormatter.timeZone = TimeZone.getTimeZone("America/Halifax")
                    val currentDateTime = dateFormatter.format(Date())

                    val scoreEntry = "$difficulty--$currentDateTime--$score"

                    db.collection("users")
                        .document(email)
                        .update("quizScores", FieldValue.arrayUnion(scoreEntry))
                        .await()

                    Result.success(Unit)
                }
            } catch (e: TimeoutCancellationException) {
                Result.failure(Exception("Unable to record quiz score. Check your internet connection."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncUsageTime(email: String, usageTime: Long) {

        val usageTimeInMinutes = usageTime / 60000

        return withContext(Dispatchers.IO) {
            try {
                withTimeout(15000L) {
                    val dateFormatter = SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss", Locale.getDefault())
                    dateFormatter.timeZone = TimeZone.getTimeZone("America/Halifax")
                    val currentDateTime = dateFormatter.format(Date())

                    val usageTimeMap = mapOf(
                        "time" to usageTimeInMinutes,
                        "lastUpdated" to currentDateTime
                    )

                    db.collection("users")
                        .document(email)
                        .update("usageTime", usageTimeMap)
                        .await()

                    Log.d("Usage Tracker", "Successfully saved usage time and timestamp to Firebase.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.d("Usage Tracker", "Time out while trying to save usage time to Firebase.")
            } catch (e: Exception) {
                Log.d("Usage Tracker", "Failed to save usage time to Firebase.")
            }
        }
    }








}
