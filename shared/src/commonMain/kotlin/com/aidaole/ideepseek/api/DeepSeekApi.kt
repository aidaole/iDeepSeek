package com.aidaole.ideepseek.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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
        stream: Boolean = true
    ): Result<ChatResponse>

    // 添加新的流式聊天方法
    suspend fun chatStream(
        messages: List<ChatMessage>,
        model: String = "deepseek-chat",
        temperature: Float = 0.7f,
        topP: Float = 0.7f,
        maxTokens: Int = 2048,
        onResponse: (StreamResponse) -> Unit
    )

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
        val index: Int, val message: ChatMessage, val finish_reason: String?
    )

    @Serializable
    data class TokenUsage(
        val prompt_tokens: Int, val completion_tokens: Int, val total_tokens: Int
    )

    @Serializable
    data class StreamResponse(
        val id: String,
        @SerialName("object") val objectName: String,
        val created: Long,
        val model: String,
        @SerialName("system_fingerprint") val systemFingerprint: String,
        val choices: List<StreamChoice>,
        val usage: Usage? = null
    )

    @Serializable
    data class StreamChoice(
        val index: Int,
        val delta: DeltaMessage,
        val logprobs: JsonElement? = null,
        @SerialName("finish_reason") val finishReason: String?
    )

    @Serializable
    data class DeltaMessage(
        val role: String? = null, val content: String? = null
    )

    @Serializable
    data class Usage (
        @SerialName("prompt_tokens")
        val promptTokens: Long,

        @SerialName("completion_tokens")
        val completionTokens: Long,

        @SerialName("total_tokens")
        val totalTokens: Long,

        @SerialName("prompt_tokens_details")
        val promptTokensDetails: PromptTokensDetails,

        @SerialName("prompt_cache_hit_tokens")
        val promptCacheHitTokens: Long,

        @SerialName("prompt_cache_miss_tokens")
        val promptCacheMissTokens: Long
    )

    @Serializable
    data class PromptTokensDetails (
        @SerialName("cached_tokens")
        val cachedTokens: Long
    )
}