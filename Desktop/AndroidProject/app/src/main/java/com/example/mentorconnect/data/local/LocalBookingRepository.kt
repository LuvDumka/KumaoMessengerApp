package com.example.mentorconnect.data.local

import com.example.mentorconnect.data.model.SlotStatus
import com.example.mentorconnect.data.model.TimeSlot

/**
 * Local booking repository - uses in-memory storage
 * Returns Result<T> to match BookingRepository interface
 */
class LocalBookingRepository {
    
    suspend fun getAllSlotsForDate(mentorId: String, date: String): Result<List<TimeSlot>> {
        return try {
            val slots = LocalDataStore.getSlotsForMentor(mentorId, date)
            Result.success(slots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvailableSlots(mentorId: String, date: String): Result<List<TimeSlot>> {
        return try {
            val slots = LocalDataStore.getSlotsForMentor(mentorId, date)
                .filter { it.status == SlotStatus.AVAILABLE }
            Result.success(slots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun bookSlot(
        slotId: String,
        userId: String,
        userName: String,
        studentMessage: String
    ): Result<Boolean> {
        return try {
            LocalDataStore.bookSlot(slotId, userId, userName, studentMessage)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelBooking(slotId: String): Result<Boolean> {
        return try {
            LocalDataStore.cancelBooking(slotId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMyBookings(userId: String): Result<List<TimeSlot>> {
        return try {
            val bookings = LocalDataStore.getMyBookings(userId)
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMentorSessions(mentorId: String): Result<List<TimeSlot>> {
        return try {
            val sessions = LocalDataStore.getMentorSessions(mentorId)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
