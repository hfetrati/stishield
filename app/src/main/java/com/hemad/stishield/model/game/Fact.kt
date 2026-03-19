package com.hemad.stishield.model.game

import kotlinx.serialization.Serializable

@Serializable
data class Fact(
    val id:Int,
    val title:String,
    val description: String
)