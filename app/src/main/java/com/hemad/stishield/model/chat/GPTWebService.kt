package com.hemad.stishield.model.chat

import android.util.Log
import com.hemad.stishield.model.common.ChatItem
import com.hemad.stishield.model.common.Message
import com.hemad.stishield.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.json.JSONArray
import org.json.JSONObject

class GPTWebService(persona:String) {

    //Set your own prompt IDs and API Key in local.properties
    private val OBINNA_PROMPT_ID = BuildConfig.FIRST_PROMPT_ID
    private val HANA_PROMPT_ID = BuildConfig.SECOND_PROMPT_ID
    private val OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY


    private val currentPersona = persona
    private var activePromptId = if (currentPersona == "obinna") OBINNA_PROMPT_ID else  HANA_PROMPT_ID
    private var previousResponseId: String? = null

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 120_000
        }
    }

    suspend fun getResponse(inputMessage: String): ChatItem.MessageItem {
        try {
            val requestBody = buildResponsesRequest(inputMessage).toString()

            val response = client.post("https://api.openai.com/v1/responses") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $OPENAI_API_KEY")
                setBody(requestBody)
            }

            val responseText = response.bodyAsText()

            if (response.status.value !in 200..299) {
                Log.e("GPTWebService", "OpenAI error: ${response.status.value} $responseText")
                throw Exception("OpenAI request failed: ${response.status.value} $responseText")
            }

            val json = JSONObject(responseText)

            previousResponseId = json.optString("id", null)

            val outputText = extractOutputText(json)
                ?: throw Exception("No text output found in response: $responseText")

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

    private fun buildResponsesRequest(inputMessage: String): JSONObject {
        val json = JSONObject()

        json.put("model", "gpt-4.1-mini")
        json.put("store", true)

        val promptObject = JSONObject().apply {
            put("id", activePromptId)
        }
        json.put("prompt", promptObject)

        val inputArray = JSONArray().apply {
            put(
                JSONObject().apply {
                    put("role", "user")
                    put("content", inputMessage)
                }
            )
        }
        json.put("input", inputArray)

        previousResponseId?.let {
            json.put("previous_response_id", it)
        }

        return json
    }

    private fun extractOutputText(json: JSONObject): String? {
        val directText = json.optString("output_text", null)
        if (!directText.isNullOrBlank()) return directText

        val outputArray = json.optJSONArray("output") ?: return null
        val builder = StringBuilder()

        for (i in 0 until outputArray.length()) {
            val outputItem = outputArray.optJSONObject(i) ?: continue

            if (outputItem.optString("type") == "message") {
                val contentArray = outputItem.optJSONArray("content") ?: continue

                for (j in 0 until contentArray.length()) {
                    val contentItem = contentArray.optJSONObject(j) ?: continue

                    if (contentItem.optString("type") == "output_text") {
                        builder.append(contentItem.optString("text"))
                    }
                }
            }
        }

        return builder.toString().trim().ifBlank { null }
    }

    fun clearHistory() {
        previousResponseId = null
    }

    fun close() {
        client.close()
    }


}
