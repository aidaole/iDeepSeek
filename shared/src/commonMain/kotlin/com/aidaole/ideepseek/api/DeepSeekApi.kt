package com.aidaole.ideepseek.api

interface DeepSeekApi {
    // Token 相关
    suspend fun setApiToken(token: String)
    suspend fun getApiToken(): String?
    suspend fun clearApiToken()
    
    // Chat 相关
    suspend fun chat(
        messages: List<ChatMessage>,
        temperature: Float = 0.7f,
        topP: Float = 0.7f,
        maxTokens: Int = 2048
    ): Result<ChatResponse>
    
    data class ChatMessage(
        val role: String, // "user" 或 "assistant"
        val content: String
    )
    
    data class ChatResponse(
        val id: String,
        val message: ChatMessage,
        val usage: TokenUsage
    )
    
    data class TokenUsage(
        val promptTokens: Int,
        val completionTokens: Int,
        val totalTokens: Int
    )
} 