package com.hemad.stishield.model.game

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Task(
    val id:Int,
    val description: String,
    val choices:List<String> = emptyList(),
    val correctChoiceIndex:Int?=null,
    val enemyName:String?=null,
    val isFinalTask:Boolean?=null,
    @Transient
    var action: ((String) -> GameResult?)? = null,
    @Transient
    var imageID:Int? = null
)

sealed class GameResult {
    data class Success(val content: Any?=null) : GameResult()
    data class Failure(val content: Any?=null) : GameResult()
    data class Continue(val content: Any?=null) : GameResult()
    data class Repeat(val content: Any?=null) : GameResult()
    data class Achievement(val content: Any?=null) : GameResult()
}