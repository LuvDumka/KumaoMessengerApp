package com.example.mentorconnect.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeSlot(
    val id: String = "",
    val mentorId: String = "",
    val date: String = "", // Format: yyyy-MM-dd
    val startTime: String = "", // Format: HH:mm
    val endTime: String = "", // Format: HH:mm
    val status: SlotStatus = SlotStatus.AVAILABLE,
    val bookedByUserId: String = "", // User ID
    val bookedByUserName: String = "",
    val bookedAt: Long = 0L,
    val price: Double = 0.0,
    val studentMessage: String = ""
) : Parcelable

enum class SlotStatus {
    AVAILABLE,
    BOOKED,
    COMPLETED,
    CANCELLED
}
