package com.aidaole.ideepseek.api

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AndroidDeepSeekApi(private val tokenManager: TokenManager) : DeepSeekApi {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val apiUrl = "https://api.deepseek.com/v1/chat/completions"

    override suspend fun setApiToken(token: String) {
        tokenManager.saveToken(token)
    }

    override suspend fun getApiToken(): String? {
        return tokenManager.getToken()
    }

    override suspend fun clearApiToken() {
        tokenManager.clearToken()
    }

    override suspend fun chat(
        messages: List<DeepSeekApi.ChatMessage>,
        model: String,
        temperature: Float,
        topP: Float,
        maxTokens: Int,
        stream: Boolean
    ): Result<DeepSeekApi.ChatResponse> = runCatching {
        val token = tokenManager.getToken() ?: throw IllegalStateException("API Token not set")

        // 构建请求体
        val request = DeepSeekApi.ChatRequest(
            model = model,
            messages = messages,
            temperature = temperature,
            top_p = topP,
            max_tokens = maxTokens,
            stream = stream
        )

        // 发送请求并指定返回类型
        client.post(apiUrl) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(request)
        }.body<DeepSeekApi.ChatResponse>()
    }
}