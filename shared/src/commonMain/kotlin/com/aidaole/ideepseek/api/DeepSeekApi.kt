package com.aidaole.ideepseek.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface DeepSeekApi {
    // Token 相关
    suspend fun setApiToken(token: String)
    suspend fun getApiToken(): String?
    suspend fun clearApiToken()
    
    // Chat 相关
    suspend fun chat(
        messages: List<ChatMessage>,
        model: String = "deepseek-chat",
        temperature: Float = 0.7f,
        topP: Float = 0.7f,
        maxTokens: Int = 2048,
        stream: Boolean = false
    ): Result<ChatResponse>
    
    @Serializable
    data class ChatMessage(
        val role: String,     // "system", "user", "assistant"
        val content: String
    )
    
    @Serializable
    data class ChatRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val temperature: Float? = null,
        val top_p: Float? = null,
        val max_tokens: Int? = null,
        val stream: Boolean = false
    )
    
    @Serializable
    data class ChatResponse(
        val id: String,
        @SerialName("object") val objectName: String,
        val created: Long,
        val model: String,
        val choices: List<Choice>,
        val usage: TokenUsage
    )
    
    @Serializable
    data class Choice(
        val index: Int,
        val message: ChatMessage,
        val finish_reason: String?
    )
    
    @Serializable
    data class TokenUsage(
        val prompt_tokens: Int,
        val completion_tokens: Int,
        val total_tokens: Int
    )
} 