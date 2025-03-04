package com.aidaole.ideepseek.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class CommonDeepSeekApi(
    val tokenManager: TokenManager,
    val client: HttpClient
): DeepSeekApi {
    private val apiUrl = "https://api.deepseek.com/v1/chat/completions"
    private val customJson = Json {
        ignoreUnknownKeys = true
        isLenient = true // 可选：宽松解析
    }

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

        val response = client.post(apiUrl) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(request)
        }
        println("Response status: ${response.status}")  // 日志
        response.body<DeepSeekApi.ChatResponse>()
    }

    override suspend fun chatStream(
        messages: List<DeepSeekApi.ChatMessage>,
        model: String,
        temperature: Float,
        topP: Float,
        maxTokens: Int,
        onResponse: (DeepSeekApi.StreamResponse) -> Unit
    ) {
        val token = tokenManager.getToken() ?: throw IllegalStateException("API Token not set")

        val request = DeepSeekApi.ChatRequest(
            model = model,
            messages = messages,
            temperature = temperature,
            top_p = topP,
            max_tokens = maxTokens,
            stream = true
        )

        client.preparePost(apiUrl) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(request)
        }.execute { response ->
            response.bodyAsChannel().apply {
                val buffer = StringBuilder()
                while (!isClosedForRead) {
                    val line = readUTF8Line(1000) ?: continue
                    println("chatStream: $line")

                    if (line.isBlank()) {
                        // 处理缓冲区中的数据
                        val content = buffer.toString()
                        buffer.clear()

                        if (content.startsWith("data: ")) {
                            val json = content.substring(6).trim() // 去掉 "data: " 前缀
                            if (json == "[DONE]") break
                            try {
                                val streamResponse = customJson.decodeFromString<DeepSeekApi.StreamResponse>(json)
                                onResponse(streamResponse)
                            } catch (e: Exception) {
                                println("Parse error: $json, $e")
                            }
                        }
                    } else {
                        // 累积数据到缓冲区
                        buffer.append(line)
                    }
                }
            }
        }
    }
}