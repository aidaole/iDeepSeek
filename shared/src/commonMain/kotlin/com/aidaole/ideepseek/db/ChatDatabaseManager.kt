package com.aidaole.ideepseek.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ChatDatabaseManager(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = ChatDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.chatQueries

    suspend fun createChatSession(title: String): Long = withContext(Dispatchers.Default) {
        dbQuery.transactionWithResult {
            dbQuery.insertChatSession(title, Clock.System.now().toEpochMilliseconds())
            dbQuery.lastInsertRowId().executeAsOne()
        }
    }

    suspend fun addMessage(
        sessionId: Long, role: String, content: String
    ) = withContext(Dispatchers.Default) {
        dbQuery.insertChatMessage(
            session_id = sessionId,
            role = role,
            content = content,
            created_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    fun getSessionList(): List<ChatSession> {
        return dbQuery.getAllSessions().executeAsList()
    }

    fun getAllSessions(): Flow<List<ChatSession>> {
        return dbQuery.getAllSessions().asFlow().mapToList(Dispatchers.Default)
    }

    fun getSessionMessages(sessionId: Long): Flow<List<ChatMessage>> {
        return dbQuery.getSessionMessages(sessionId).asFlow().mapToList(Dispatchers.Default)
    }

    suspend fun deleteChatSession(sessionId: Long) = withContext(Dispatchers.Default) {
        dbQuery.deleteChatSession(sessionId)
    }
} 