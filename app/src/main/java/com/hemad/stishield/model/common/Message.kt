package com.hemad.stishield.model.common

data class Message(
    val id:Int? = null,
    val body: String,
    val role: SenderRole,
    val buttonTitles:List<String>? = null
) {
    enum class SenderRole {
        USER,
        BOT
    }
}
