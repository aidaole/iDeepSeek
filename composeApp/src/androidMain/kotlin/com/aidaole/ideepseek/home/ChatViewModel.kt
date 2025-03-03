package com.aidaole.ideepseek.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidaole.ideepseek.api.DeepSeekApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val deepSeekApi: DeepSeekApi
) : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
    }
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val token = deepSeekApi.getApiToken()
                if (token != null) {
                    _uiState.value = UiState.TokenSet
                } else {
                    _uiState.value = UiState.NeedToken
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("获取 Token 失败: ${e.message}")
            }
        }
    }

    fun setApiToken(token: String) {
        viewModelScope.launch {
            try {
                deepSeekApi.setApiToken(token)
                _uiState.value = UiState.TokenSet
            } catch (e: Exception) {
                _uiState.value = UiState.Error("设置 Token 失败: ${e.message}")
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            try {
                // 添加用户消息
                val userMessage = ChatMessage(content = content, isUser = true)
                _messages.add(userMessage)
                
                // 添加一个空的 AI 消息用于流式更新
                val aiMessage = ChatMessage("", isUser = false)
                _messages.add(aiMessage)
                
                // 构建消息历史
                val apiMessages = buildMessageHistory()
                
                // 调用流式 API
                deepSeekApi.chatStream(
                    messages = apiMessages,
                    onResponse = { streamResponse ->
                        val content = streamResponse.choices.firstOrNull()?.delta?.content
                        if (content != null) {
                            // 更新最后一条 AI 消息
                            val lastIndex = _messages.lastIndex
                            _messages[lastIndex] = _messages[lastIndex].copy(
                                content = _messages[lastIndex].content + content
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("发送消息失败: ${e.message}")
            }
        }
    }
    
    private fun buildMessageHistory(): List<DeepSeekApi.ChatMessage> {
        val history = mutableListOf<DeepSeekApi.ChatMessage>()
        
        // 添加系统消息
        history.add(DeepSeekApi.ChatMessage(
            role = "system",
            content = "You are a helpful assistant."
        ))
        
        // 添加历史对话
        _messages.forEach { message ->
            history.add(DeepSeekApi.ChatMessage(
                role = if (message.isUser) "user" else "assistant",
                content = message.content
            ))
        }
        
        return history
    }

    sealed class UiState {
        object Initial : UiState()
        object NeedToken : UiState()
        object TokenSet : UiState()
        data class Error(val message: String) : UiState()
    }
}