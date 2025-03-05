package com.aidaole.ideepseek.home

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val isLoading: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) 