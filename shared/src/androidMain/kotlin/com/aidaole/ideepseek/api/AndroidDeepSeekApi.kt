package com.aidaole.ideepseek.api

class AndroidDeepSeekApi(val tokenManager: TokenManager) : DeepSeekApi {

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
        messages: List<DeepSeekApi.ChatMessage>, temperature: Float, topP: Float, maxTokens: Int
    ): Result<DeepSeekApi.ChatResponse> {
        return Result.success(
            DeepSeekApi.ChatResponse(
                "1", DeepSeekApi.ChatMessage("my", "yes"), DeepSeekApi.TokenUsage(1, 1, 1)
            )
        )
    }
}