package com.aidaole.ideepseek.api

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

actual class TokenManager(private val context: Context) {
    companion object {
        private const val TAG = "TokenManager"
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "deepseek_secrets",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual suspend fun saveToken(token: String) {
        sharedPreferences.edit().putString("api_token", token).apply()
        Log.d(TAG, "saveToken: $token")
    }

    actual suspend fun getToken(): String? {
        val token = sharedPreferences.getString("api_token", null)
        Log.d(TAG, "getToken: $token")
        return token
    }

    actual suspend fun clearToken() {
        Log.d(TAG, "clearToken: ")
        sharedPreferences.edit().remove("api_token").apply()
    }
} 