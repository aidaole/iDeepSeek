package com.aidaole.ideepseek.api

actual class TokenManager {
    private val keychain = KeychainWrapper()

    actual suspend fun saveToken(token: String) {
        keychain.set(token, forKey = "api_token")
    }

    actual suspend fun getToken(): String? {
        return keychain.string(forKey = "api_token")
    }

    actual suspend fun clearToken() {
        keychain.removeObject(forKey = "api_token")
    }
} 