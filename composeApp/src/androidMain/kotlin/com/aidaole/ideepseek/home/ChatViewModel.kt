package com.aidaole.ideepseek.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidaole.ideepseek.api.DeepSeekApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val deepSeekApi: DeepSeekApi
) : ViewModel() {
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
                _messages.add(ChatMessage(content = content, isUser = true))

                // 调用 API
                val response = deepSeekApi.chat(
                    messages = _messages.map {
                        DeepSeekApi.ChatMessage(
                            role = if (it.isUser) "user" else "assistant",
                            content = it.content
                        )
                    }
                ).getOrThrow()

                // 添加 AI 回复
                _messages.add(ChatMessage(
                    content = response.message.content,
                    isUser = false
                ))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("发送消息失败: ${e.message}")
            }
        }
    }

    sealed class UiState {
        object Initial : UiState()
        object NeedToken : UiState()
        object TokenSet : UiState()
        data class Error(val message: String) : UiState()
    }
}