package com.hemad.stishield.model.gpt

import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message

class GPTRepository(persona: String) {

    private val gpt_service: GPTWebService = GPTWebService(persona)
    val currentPersona = persona
    val FIRST_MESSAGE_OBINNA = "Hey there! I’m Dr. Obinna, your go-to doctor for all things related to sexual health. I'm here to provide you with reliable advice and information on a range of topics including sexual and reproductive health, sexual dysfunction, orientation, identity, consent, and much more. If you have any concerns or questions related to these areas, feel free to ask. Our chat is completely confidential and will be deleted after out talk ends."
    val FIRST_MESSAGE_HANA = "Hi! I'm Dr. Hana, and my specialty is sexual health. I'm here to offer guidance and support on a variety of topics including sexual and reproductive health, sexual dysfunction, orientation, identity, consent, and more. Remember, our conversations are private and everything will be deleted after our chat so feel free to discuss your concerns."


    fun loadFirstMessage():ChatItem.MessageItem{

        return if (currentPersona == "obinna") {
            ChatItem.MessageItem(Message(body = FIRST_MESSAGE_OBINNA, role = Message.SenderRole.BOT))
        } else {
            ChatItem.MessageItem(Message(body = FIRST_MESSAGE_HANA, role = Message.SenderRole.BOT))
        }

    }

    suspend fun getGPTResponse(message: String): ChatItem.MessageItem {

        try {
            val response = gpt_service.getResponse(message)
            return response

        } catch (error: Exception) {
            throw error("Unable to get a response. Check your internet connection.")
        }


    }

    fun clearHistory(){
        gpt_service.clearHistory()
    }

    fun close() {
        gpt_service.close()
    }


}

