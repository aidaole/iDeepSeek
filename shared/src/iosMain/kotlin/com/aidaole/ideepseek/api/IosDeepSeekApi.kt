package com.aidaole.ideepseek.api

import com.aidaole.ideepseek.db.DatabaseDriverFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class IosDeepSeekApi(private val tokenManager: TokenManager) : DeepSeekApi {
    companion object {
        private const val TAG = "IosDeepSeekApi"
    }

    private val customJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // 使用 Darwin 引擎，这是 iOS 平台的 HTTP 客户端引擎
    private val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(customJson)
        }
        
        // iOS 特定的配置
        engine {
            configureRequest {
                setAllowsCellularAccess(true)
                setTimeoutInterval(30.0)
            }
        }
    }

    // 使用通用实现
    private val commonDeepSeekApi = CommonDeepSeekApi(tokenManager, client, DatabaseDriverFactory())

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