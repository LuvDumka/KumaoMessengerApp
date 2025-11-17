package com.example.mentorconnect.data.repository

import android.util.Log
import com.example.mentorconnect.BuildConfig
import com.example.mentorconnect.data.model.ChatMessage
import com.example.mentorconnect.data.model.ChatThread
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(
    private val realtimeDb: FirebaseDatabase = createDatabaseInstance()
) {

    private val messagesRef = realtimeDb.reference.child(MESSAGES_NODE)
    private val threadsRef = realtimeDb.reference.child(THREADS_NODE)

    fun observeThreads(userId: String): Flow<List<ChatThread>> = callbackFlow {
        val userThreadsRef = threadsRef.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val threads = snapshot.children.mapNotNull { child ->
                    child.getValue(ChatThread::class.java)
                }.sortedByDescending { it.lastMessageTimestamp }
                trySend(threads)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userThreadsRef.addValueEventListener(listener)
        awaitClose { userThreadsRef.removeEventListener(listener) }
    }

    fun getMessages(userId: String, otherUserId: String): Flow<List<ChatMessage>> = callbackFlow {
        val conversationId = conversationId(userId, otherUserId)
        val conversationRef = messagesRef.child(conversationId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { child ->
                    child.getValue(ChatMessage::class.java)?.copy(
                        id = child.key.orEmpty(),
                        conversationId = conversationId
                    )
                }.sortedBy { it.timestamp }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        conversationRef.addValueEventListener(listener)
        awaitClose { conversationRef.removeEventListener(listener) }
    }

    suspend fun sendMessage(message: ChatMessage, receiverName: String): Result<String> {
        return try {
            val conversationId = conversationId(message.senderId, message.receiverId)
            val newMessageRef = messagesRef.child(conversationId).push()
            val messageId = newMessageRef.key ?: System.currentTimeMillis().toString()

            val payload = message.copy(
                id = messageId,
                conversationId = conversationId
            )

            newMessageRef.setValue(payload).await()

            updateThreads(payload, receiverName)

            Result.success(messageId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateThreads(message: ChatMessage, receiverName: String) {
        val senderThread = ChatThread(
            conversationId = message.conversationId,
            otherUserId = message.receiverId,
            otherUserName = receiverName,
            lastMessage = message.message,
            lastMessageTimestamp = message.timestamp,
            unreadCount = 0
        )

        val receiverThread = ChatThread(
            conversationId = message.conversationId,
            otherUserId = message.senderId,
            otherUserName = message.senderName,
            lastMessage = message.message,
            lastMessageTimestamp = message.timestamp,
            unreadCount = 0
        )

        threadsRef.child(message.senderId)
            .child(message.conversationId)
            .setValue(senderThread)
            .await()

        threadsRef.child(message.receiverId)
            .child(message.conversationId)
            .setValue(receiverThread)
            .await()
    }

    private fun conversationId(userA: String, userB: String): String {
        return listOf(userA, userB).sorted().joinToString(separator = "_")
    }

    companion object {
        private const val MESSAGES_NODE = "messages"
        private const val THREADS_NODE = "chatThreads"

        private fun createDatabaseInstance(): FirebaseDatabase {
            return runCatching {
                val url = BuildConfig.REALTIME_DB_URL
                if (url.isNullOrBlank()) {
                    FirebaseDatabase.getInstance()
                } else {
                    FirebaseDatabase.getInstance(url)
                }
            }.getOrElse {
                FirebaseDatabase.getInstance().apply {
                    android.util.Log.e(
                        "ChatRepository",
                        "Falling back to default Realtime DB: ${it.localizedMessage}"
                    )
                }
            }
        }
    }
}
