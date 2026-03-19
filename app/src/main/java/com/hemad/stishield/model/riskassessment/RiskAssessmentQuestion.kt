package com.hemad.stishield.model.riskassessment
import kotlinx.serialization.Serializable

@Serializable
data class RiskAssessmentQuestion(
    val id: Int,
    val text: String,
    val responses: List<String>
)