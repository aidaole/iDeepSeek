package com.aidaole.ideepseek.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        // 添加用户消息
        _messages.add(ChatMessage(content = content, isUser = true))
        
        // 模拟AI回复
        _messages.add(ChatMessage(
            content = "我收到了你的消息: $content",
            isUser = false
        ))
    }
} 