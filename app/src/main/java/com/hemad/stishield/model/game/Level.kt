package com.hemad.stishield.model.game

import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val id:Int,
    val title:String,
    val tasks: List<Task>
)