package com.aidaole.ideepseek.api

expect class TokenManager {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
} 