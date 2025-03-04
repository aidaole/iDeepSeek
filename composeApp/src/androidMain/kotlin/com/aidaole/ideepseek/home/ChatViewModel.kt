package com.aidaole.ideepseek.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidaole.ideepseek.api.DeepSeekApi
import com.aidaole.ideepseek.api.CommonDeepSeekApi
import com.aidaole.ideepseek.db.ChatDatabaseManager
import com.aidaole.ideepseek.db.ChatSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val api: DeepSeekApi,
    private val dbManager: ChatDatabaseManager
) : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _tokenState = MutableStateFlow<TokenState>(TokenState.Initial)
    val tokenState: StateFlow<TokenState> = _tokenState.asStateFlow()

    private val _chatSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatSessions: StateFlow<List<ChatSession>> = _chatSessions.asStateFlow()

    private var currentSessionId = 0L

    init {
        viewModelScope.launch {
            try {
                val token = api.getApiToken()
                if (token != null) {
                    _tokenState.value = TokenState.TokenSet
                } else {
                    _tokenState.value = TokenState.NeedToken
                }
            } catch (e: Exception) {
                _tokenState.value = TokenState.Error("获取 Token 失败: ${e.message}")
            }
        }

        viewModelScope.launch {
            dbManager.getAllSessions().collect { sessions ->
                _chatSessions.value = sessions
            }
        }
    }

    fun setApiToken(token: String) {
        viewModelScope.launch {
            try {
                api.setApiToken(token)
                _tokenState.value = TokenState.TokenSet
            } catch (e: Exception) {
                _tokenState.value = TokenState.Error("设置 Token 失败: ${e.message}")
            }
        }
    }

    fun sendMessage(content: String, isDeepThink: Boolean) {
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
                api.chatStream(
                    currentSessionId,
                    model = if (!isDeepThink) "deepseek-chat" else "deepseek-reasoner",
                    messages = apiMessages,
                    onResponse = { streamResponse ->
                        val content = streamResponse.choices.firstOrNull()?.delta?.content
                        val reasoningContent =
                            streamResponse.choices.firstOrNull()?.delta?.reasoningContent
                        if (content != null) {
                            // 更新最后一条 AI 消息
                            val lastIndex = _messages.lastIndex
                            _messages[lastIndex] = _messages[lastIndex].copy(
                                content = _messages[lastIndex].content + content
                            )
                        } else if (reasoningContent != null) {
                            val lastIndex = _messages.lastIndex
                            _messages[lastIndex] = _messages[lastIndex].copy(
                                content = _messages[lastIndex].content + reasoningContent
                            )
                        }
                    })
            } catch (e: Exception) {
                _tokenState.value = TokenState.Error("发送消息失败: ${e.message}")
            }
        }
    }

    private fun buildMessageHistory(): List<DeepSeekApi.ChatMessage> {
        val history = mutableListOf<DeepSeekApi.ChatMessage>()
        // 添加历史对话
        _messages.filter {
            it.content.isNotEmpty()
        }.forEach { message ->
            history.add(
                DeepSeekApi.ChatMessage(
                    role = if (message.isUser) "user" else "assistant", content = message.content
                )
            )
        }
        return history
    }

    fun loadChatSession(sessionId: Long) {
        viewModelScope.launch {
            dbManager.getSessionMessages(sessionId).collect { chatMessages ->
                val messages = chatMessages.map {
                    ChatMessage(it.content, it.role=="user")
                }
                _messages.addAll(messages)
                currentSessionId = sessionId
            }
        }
    }

    // 创建新会话
    fun createNewChat() {
        _messages.clear()
//        viewModelScope.launch {
//            currentSessionId = dbManager.getSessionList().size.toLong()
//        }
        currentSessionId = 0L
    }

    sealed class TokenState {
        data object Initial : TokenState()
        data object NeedToken : TokenState()
        data object TokenSet : TokenState()
        data class Error(val message: String) : TokenState()
    }
}