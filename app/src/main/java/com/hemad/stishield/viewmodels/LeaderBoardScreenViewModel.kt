package com.hemad.stishield.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hemad.stishield.model.common.UserDataRepository
import com.hemad.stishield.model.leaderboard.LeaderBoardData
import com.hemad.stishield.model.leaderboard.LeaderBoardRow
import kotlinx.coroutines.launch

class LeaderBoardScreenViewModel(context: Context): ViewModel() {

    var dataTable = mutableStateOf<List<LeaderBoardRow>?>(null)
    val repository = UserDataRepository(context)
    val processState = mutableStateOf( false)
    var showInfoPopup = mutableStateOf(false)
    val errorState = mutableStateOf<String?>(null)
    val tableTitles = listOf("Rank","Nickname","⭐")
    val userPoints = repository.points
    val userEmail = repository.email
    var userRank = mutableStateOf<String>("N/A")
    val infoTitle = "Ready to boost your rank?"
    val infoText = "<b>\uD83D\uDCDC Find each scroll of health in Knowledge Quest</b>\nEvery scroll you discover rewards you with 100 ⭐!\n\n<b>\uD83D\uDCDD Answer all quiz questions correctly in STI Smart</b>\nShow off your knowledge and earn another 100 ⭐!\n\n<b>\uD83D\uDEE1\uFE0F Complete an evaluation in the Health Guard module</b>\nComplete it for the first time and grab 100 ⭐!\n\n<b>⏳ Spend more time in the app</b>\nEach 5 consecutive minutes of usage, whether you're chatting with our virtual doctors in Safe Talk or exploring other app modules, earns you 10 ⭐!\n\nHappy exploring!"



    fun syncUserPointsAndLoadLeaderboard(){

        viewModelScope.launch {
            processState.value = true
            val pointSyncResult = repository.updateUserPoints()
            processState.value = false
            pointSyncResult.onSuccess {
                processState.value = true
                val loadLeaderboardResult = repository.getLeaderboardData()
                processState.value = false
                loadLeaderboardResult.onSuccess { leaderboard ->
                    dataTable.value = convertToLeaderBoardRows(leaderboard)
                    val userEntry = leaderboard.find { it.email == userEmail }
                    userRank.value = userEntry?.rank?.toString() ?: "N/A"
                }.onFailure { exception ->
                    errorState.value = exception.message
                }
            }.onFailure { exception ->
                errorState.value = exception.message
            }
        }

    }

    fun clearError() {
        errorState.value = null
    }

    fun convertToLeaderBoardRows(dataList: List<LeaderBoardData>): List<LeaderBoardRow> {
        return dataList.map { data ->
            LeaderBoardRow(
                rank = data.rank,
                nickname = data.nickname,
                points = data.points
            )
        }
    }


}