package com.example.mentorconnect.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val conversationId: String = "",
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT,
    val imageUrl: String = "",
    val isRead: Boolean = false
) : Parcelable

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO_CALL_REQUEST,
    VIDEO_CALL_ENDED
}
