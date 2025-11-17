package com.example.mentorconnect.data.local

import com.example.mentorconnect.data.model.ChatMessage
import com.example.mentorconnect.data.model.ChatThread
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Local chat repository - uses in-memory storage
 */
class LocalChatRepository {
    
    fun observeThreads(userId: String): Flow<List<ChatThread>> = callbackFlow {
        // Send initial threads
        trySend(LocalDataStore.getChatThreads(userId))
        
        // In a real implementation, you'd watch for changes
        // For now, we'll just send once
        awaitClose()
    }
    
    fun getMessages(currentUserId: String, otherUserId: String): Flow<List<ChatMessage>> = callbackFlow {
        // Build conversation ID the same way ChatRepository does
        val conversationId = conversationId(currentUserId, otherUserId)
        // Send initial messages
        trySend(LocalDataStore.getMessages(conversationId))
        
        // In a real implementation, you'd watch for changes
        awaitClose()
    }
    
    suspend fun sendMessage(
        message: ChatMessage,
        receiverName: String
    ): Result<String> {
        return try {
            val conversationId = conversationId(message.senderId, message.receiverId)
            val messageWithId = message.copy(
                id = "msg_${System.currentTimeMillis()}",
                conversationId = conversationId
            )
            LocalDataStore.saveMessage(conversationId, messageWithId)
            Result.success(messageWithId.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun conversationId(userId1: String, userId2: String): String {
        val sorted = listOf(userId1, userId2).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }
}
