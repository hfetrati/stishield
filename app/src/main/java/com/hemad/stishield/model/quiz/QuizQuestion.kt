package com.hemad.stishield.model.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestion(
    val id:Int,
    val text: String,
    val choices:List<String>,
    val correctChoiceIndex:Int?=null,
    val explanation:String?=null
)