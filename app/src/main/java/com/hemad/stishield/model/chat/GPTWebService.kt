package com.hemad.stishield.model.chat

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.client.OpenAI
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import kotlinx.coroutines.delay
import com.hemad.stishield.BuildConfig

class GPTWebService(persona:String) {

    //Replace with your own assistant IDs,
    val OBINNA_ASSISTANT_ID = BuildConfig.FIRST_ASSISTANT_ID
    val HANA_ASSISTANT_ID = BuildConfig.SECOND_ASSISTANT_ID
    val OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY


    @OptIn(BetaOpenAI::class)
    var thread: Thread? = null
    @OptIn(BetaOpenAI::class)
    var assistant: Assistant? = null
    val openAI = OpenAI(OPENAI_API_KEY)
    val currentPersona = persona
    var assistantID = if (currentPersona == "obinna") OBINNA_ASSISTANT_ID else  HANA_ASSISTANT_ID


    @OptIn(BetaOpenAI::class)
    suspend fun getResponse(inputMessage: String): ChatItem.MessageItem {

        try {
            assistant =
                assistant ?: openAI.assistant(id = AssistantId(assistantID))
            thread = thread ?: openAI.thread()

            openAI.message(
                threadId = thread!!.id,
                request = MessageRequest(
                    role = Role.User,
                    content = inputMessage
                )
            )

            val run = openAI.createRun(
                thread!!.id,
                request = RunRequest(
                    assistantId = assistant!!.id,
                )
            )

            do {
                delay(1500)
                val retrievedRun = openAI.getRun(threadId = thread!!.id, runId = run.id)
            } while (retrievedRun.status != Status.Completed)

            val assistantMessages = openAI.messages(thread!!.id)
            val lastMessage = assistantMessages.first()
            val result = lastMessage.content.first() as? MessageContent.Text
                ?: error("Unexpected response content.")

            return ChatItem.MessageItem(Message(body = result.text.value, role = Message.SenderRole.BOT))


        } catch (error: Exception) {
            Log.d("GPTService", error.toString())
            throw error
        }


    }


    @OptIn(BetaOpenAI::class)
    suspend fun clearHistory(){
        thread?.let { openAI.delete(id = it.id) }
    }


}