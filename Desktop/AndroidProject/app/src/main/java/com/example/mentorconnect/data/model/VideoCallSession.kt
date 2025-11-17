package com.example.mentorconnect.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoCallSession(
    val id: String = "",
    val slotId: String = "",
    val hostUserId: String = "",
    val guestUserId: String = "",
    val channelName: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val status: CallStatus = CallStatus.PENDING
) : Parcelable

enum class CallStatus {
    PENDING,
    ACTIVE,
    ENDED,
    CANCELLED
}
