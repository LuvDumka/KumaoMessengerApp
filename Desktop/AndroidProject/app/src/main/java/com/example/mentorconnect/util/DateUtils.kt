package com.example.mentorconnect.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "hh:mm a"
    private const val DATE_TIME_FORMAT = "MMM dd, yyyy hh:mm a"
    
    fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        return timeFormat.format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        val dateTimeFormat = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
        return dateTimeFormat.format(Date(timestamp))
    }
    
    fun getCurrentDate(): String {
        return formatDate(System.currentTimeMillis())
    }
    
    fun getTimeSlots(): List<String> {
        val slots = mutableListOf<String>()
        for (hour in 9..16) { // 9 AM to 5 PM (last slot starts at 4 PM)
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = if (hour > 12) hour - 12 else hour
            slots.add(String.format("%02d:00 %s", displayHour, amPm))
        }
        return slots
    }
}
