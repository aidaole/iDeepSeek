package com.aidaole.ideepseek.api

import com.aidaole.ideepseek.db.ChatDatabaseManager
import com.aidaole.ideepseek.db.DatabaseDriverFactory
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
    private val tokenManager: TokenManager,
    private val httpClient: HttpClient,
    private val dbFactory: DatabaseDriverFactory
): DeepSeekApi {
    private val apiUrl = "https://api.deepseek.com/v1/chat/completions"
    private val customJson = Json {
        ignoreUnknownKeys = true
        isLenient = true // 可选：宽松解析
    }
    private val dbManager = ChatDatabaseManager(dbFactory)
    private var currentSessionId: Long? = null

    override suspend fun setApiToken(token: String) {
        tokenManager.saveToken(token)
    }

    override suspend fun getApiToken(): String? {
        return tokenManager.getToken()
    }

    override suspend fun clearApiToken() {
        tokenManager.clearToken()
    }

    override suspend fun chatStream(
        messages: List<DeepSeekApi.ChatMessage>,
        model: String,
        temperature: Float,
        topP: Float,
        maxTokens: Int,
        onResponse: (DeepSeekApi.StreamResponse) -> Unit
    ) {
        // 如果是新对话（没有当前会话ID），才创建新的会话
        if (currentSessionId == null) {
            currentSessionId = dbManager.createChatSession(messages.last().content.take(50))
        }
        
        // 保存用户消息（只保存最新的消息）
        val sessionId = currentSessionId!!
        dbManager.addMessage(sessionId, messages.last().role, messages.last().content)

        val token = tokenManager.getToken() ?: throw IllegalStateException("API Token not set")

        val request = DeepSeekApi.ChatRequest(
            model = model,
            messages = messages,
            temperature = temperature,
            top_p = topP,
            max_tokens = maxTokens,
            stream = true
        )

        val assistantMessage = StringBuilder()
        httpClient.preparePost(apiUrl) {
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
                        val content = buffer.toString()
                        buffer.clear()

                        if (content.startsWith("data: ")) {
                            val json = content.substring(6).trim()
                            if (json == "[DONE]") {
                                // 当对话完成时保存完整的助手回复
                                dbManager.addMessage(sessionId, "assistant", assistantMessage.toString())
                                break
                            }
                            try {
                                val streamResponse = customJson.decodeFromString<DeepSeekApi.StreamResponse>(json)
                                // 累积助手的回复
                                streamResponse.choices.forEach { choice ->
                                    choice.delta.content?.let {
                                        assistantMessage.append(it)
                                    }
                                }
                                onResponse(streamResponse)
                            } catch (e: Exception) {
                                println("Parse error: $json, $e")
                            }
                        }
                    } else {
                        buffer.append(line)
                    }
                }
            }
        }
    }

    // 添加清除当前会话的方法
    fun clearCurrentSession() {
        currentSessionId = null
    }
}