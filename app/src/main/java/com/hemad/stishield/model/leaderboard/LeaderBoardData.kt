package com.hemad.stishield.model.leaderboard

data class LeaderBoardData(
    val rank:Int,
    val nickname:String,
    val points: Int,
    val email:String
)

data class LeaderBoardRow(
    val rank:Int,
    val nickname:String,
    val points: Int,
)