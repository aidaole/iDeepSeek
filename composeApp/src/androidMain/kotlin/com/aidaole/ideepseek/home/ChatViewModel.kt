package com.aidaole.ideepseek.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidaole.ideepseek.api.DeepSeekApi
import com.aidaole.ideepseek.db.ChatDatabaseManager
import com.aidaole.ideepseek.db.ChatSession
import kotlinx.coroutines.cancel
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

    private val _currentTitle = MutableStateFlow<String>("新对话")
    val currentTitle: StateFlow<String> = _currentTitle.asStateFlow()

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

        viewModelScope.launch {
            // 获取最后一个会话
            val sessions = dbManager.getSessionList()
            if (sessions.isNotEmpty()) {
                val lastSession = sessions.first() // 因为按时间倒序，第一个就是最新的
                // 检查最后一个会话是否有消息
                dbManager.getSessionMessages(lastSession.id).collect { chatMessages ->
                    if (chatMessages.isEmpty()) {
                        // 如果最后一个会话没有消息，直接使用它
                        currentSessionId = lastSession.id
                        _messages.clear()
                    } else {
                        // 如果有消息，创建新会话
                        createNewChat()
                    }
                    // 只需要收集一次，然后取消收集
                    this.cancel()
                }
            } else {
                // 如果没有任何会话，创建新会话
                createNewChat()
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

                // 如果是第一条消息，更新会话标题
                if (_messages.size == 1) {
                    val title = content.take(10) + if (content.length > 10) "..." else ""
                    dbManager.updateSessionTitle(currentSessionId, title)
                    _currentTitle.value = title
                }

                // 添加一个空的 AI 消息用于流式更新，设置loading状态为true
                val aiMessage = ChatMessage("", isUser = false, isLoading = true)
                _messages.add(aiMessage)

                // 构建消息历史
                val apiMessages = buildMessageHistory()

                Log.d(TAG, "sendMessage: $content")

                try {
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
                                // 更新最后一条 AI 消息，保持loading状态
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
                    
                    // API调用完成后，更新最后一条消息的loading状态为false
                    val lastIndex = _messages.lastIndex
                    _messages[lastIndex] = _messages[lastIndex].copy(isLoading = false)
                } catch (e: Exception) {
                    // 发生错误时，也需要更新loading状态
                    val lastIndex = _messages.lastIndex
                    _messages[lastIndex] = _messages[lastIndex].copy(isLoading = false)
                    throw e
                }
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
                    ChatMessage(it.content, it.role == "user")
                }
                _messages.clear()
                _messages.addAll(messages)
                currentSessionId = sessionId
                
                // 从会话列表中找到对应的会话标题
                _chatSessions.value.find { it.id == sessionId }?.let { session ->
                    _currentTitle.value = session.title
                }
            }
        }
    }

    // 创建新会话
    fun createNewChat() {
        _messages.clear()
        _currentTitle.value = "新对话"
        viewModelScope.launch {
            currentSessionId = dbManager.createChatSession("新对话")
        }
    }

    sealed class TokenState {
        data object Initial : TokenState()
        data object NeedToken : TokenState()
        data object TokenSet : TokenState()
        data class Error(val message: String) : TokenState()
    }
}