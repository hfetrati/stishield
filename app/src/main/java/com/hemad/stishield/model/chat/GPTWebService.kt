package com.hemad.stishield.model.chat

import android.util.Log
import com.hemad.stishield.BuildConfig
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.json.JSONObject

class GPTWebService(persona: String) {

    private val currentPersona = persona
    private var previousResponseId: String? = null

    // Replace this with your API Gateway endpoint
    private val BACKEND_CHAT_URL = BuildConfig.AWS_URL

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 120_000
        }
    }

    suspend fun getResponse(inputMessage: String): ChatItem.MessageItem {
        try {
            val requestBody = buildBackendRequest(inputMessage).toString()

            val response = client.post(BACKEND_CHAT_URL) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            val responseText = response.bodyAsText()

            if (response.status.value !in 200..299) {
                Log.e("GPTWebService", "Backend error: ${response.status.value} $responseText")
                throw Exception("Backend request failed: ${response.status.value} $responseText")
            }

            val json = JSONObject(responseText)

            previousResponseId = json.optString("responseId", null)

            val outputText = json.optString("message")
                .takeIf { it.isNotBlank() }
                ?: throw Exception("No message found in backend response: $responseText")

            return ChatItem.MessageItem(
                Message(
                    body = outputText,
                    role = Message.SenderRole.BOT
                )
            )

        } catch (e: Exception) {
            Log.e("GPTWebService", "getResponse failed", e)
            throw e
        }
    }

    private fun buildBackendRequest(inputMessage: String): JSONObject {
        return JSONObject().apply {
            put("persona", currentPersona)
            put("message", inputMessage)

            previousResponseId?.let {
                put("previousResponseId", it)
            }
        }
    }

    fun clearHistory() {
        previousResponseId = null
    }

    fun close() {
        client.close()
    }
}