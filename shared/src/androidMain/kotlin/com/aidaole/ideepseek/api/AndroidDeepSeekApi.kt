package com.aidaole.ideepseek.api

import android.content.Context
import com.aidaole.ideepseek.db.DatabaseDriverFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AndroidDeepSeekApi(private val tokenManager: TokenManager, private val context: Context) : DeepSeekApi {
    companion object {
        private const val TAG = "AndroidDeepSeekApi"
    }
    private val customJson = Json {
        ignoreUnknownKeys = true
        isLenient = true // 可选：宽松解析
    }
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(customJson)
        }
    }

    private val commonDeepSeekApi = CommonDeepSeekApi(tokenManager, client, DatabaseDriverFactory(context))

    override suspend fun setApiToken(token: String) {
        commonDeepSeekApi.setApiToken(token)
    }

    override suspend fun getApiToken(): String? {
        return commonDeepSeekApi.getApiToken()
    }

    override suspend fun clearApiToken() {
        commonDeepSeekApi.clearApiToken()
    }

    override suspend fun chatStream(
        messages: List<DeepSeekApi.ChatMessage>,
        model: String,
        temperature: Float,
        topP: Float,
        maxTokens: Int,
        onResponse: (DeepSeekApi.StreamResponse) -> Unit
    ) = commonDeepSeekApi.chatStream(
        messages, model, temperature, topP, maxTokens, onResponse
    )
}