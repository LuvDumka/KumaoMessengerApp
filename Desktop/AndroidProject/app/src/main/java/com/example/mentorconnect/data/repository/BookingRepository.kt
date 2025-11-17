package com.example.mentorconnect.data.repository

import com.example.mentorconnect.data.model.SlotStatus
import com.example.mentorconnect.data.model.TimeSlot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class BookingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val slotsCollection = firestore.collection("timeSlots")
    
    companion object {
        private const val TAG = "BookingRepository"
    }
    
    suspend fun getAvailableSlots(mentorId: String, date: String): Result<List<TimeSlot>> {
        return try {
            val snapshot = slotsCollection
                .whereEqualTo("mentorId", mentorId)
                .whereEqualTo("date", date)
                .whereEqualTo("status", SlotStatus.AVAILABLE.name)
                .get()
                .await()
            
            val slots = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TimeSlot::class.java)?.copy(id = doc.id)
            }
            Result.success(slots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllSlotsForDate(mentorId: String, date: String): Result<List<TimeSlot>> {
        return try {
            val snapshot = slotsCollection
                .whereEqualTo("mentorId", mentorId)
                .whereEqualTo("date", date)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val slots = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TimeSlot::class.java)?.copy(id = doc.id)
            }
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
            val updates = hashMapOf<String, Any>(
                "status" to SlotStatus.BOOKED.name,
                "bookedByUserId" to userId,
                "bookedByUserName" to userName,
                "bookedAt" to System.currentTimeMillis(),
                "studentMessage" to studentMessage
            )
            
            slotsCollection.document(slotId)
                .update(updates)
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelBooking(slotId: String): Result<Boolean> {
        return try {
            val updates = hashMapOf<String, Any>(
                "status" to SlotStatus.CANCELLED.name,
                "studentMessage" to "",
                "bookedByUserId" to "",
                "bookedByUserName" to "",
                "bookedAt" to 0L
            )
            
            slotsCollection.document(slotId)
                .update(updates)
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMyBookings(userId: String): Result<List<TimeSlot>> {
        return try {
            val snapshot = slotsCollection
                .whereEqualTo("bookedByUserId", userId)
                .whereIn("status", listOf(SlotStatus.BOOKED.name, SlotStatus.COMPLETED.name))
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val bookings = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TimeSlot::class.java)?.copy(id = doc.id)
            }
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMentorSessions(mentorId: String): Result<List<TimeSlot>> {
        return try {
            val snapshot = slotsCollection
                .whereEqualTo("mentorId", mentorId)
                .whereIn("status", listOf(SlotStatus.BOOKED.name, SlotStatus.COMPLETED.name))
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()

            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TimeSlot::class.java)?.copy(id = doc.id)
            }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createSlot(timeSlot: TimeSlot): Result<String> {
        return try {
            val slotData = hashMapOf(
                "mentorId" to timeSlot.mentorId,
                "date" to timeSlot.date,
                "startTime" to timeSlot.startTime,
                "endTime" to timeSlot.endTime,
                "status" to timeSlot.status.name,
                "price" to timeSlot.price,
                "bookedByUserId" to timeSlot.bookedByUserId,
                "bookedByUserName" to timeSlot.bookedByUserName,
                "bookedAt" to timeSlot.bookedAt,
                "studentMessage" to timeSlot.studentMessage
            )
            
            val docRef = slotsCollection.add(slotData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun completeSlot(slotId: String): Result<Boolean> {
        return try {
            val updates = hashMapOf<String, Any>(
                "status" to SlotStatus.COMPLETED.name
            )
            
            slotsCollection.document(slotId)
                .update(updates)
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
