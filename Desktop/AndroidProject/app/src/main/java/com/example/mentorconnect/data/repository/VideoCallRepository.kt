package com.example.mentorconnect.data.repository

import com.example.mentorconnect.data.model.CallStatus
import com.example.mentorconnect.data.model.VideoCallSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VideoCallRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val callsCollection = firestore.collection("videoCallSessions")
    
    suspend fun createCallSession(session: VideoCallSession): Result<String> {
        return try {
            val sessionData = hashMapOf(
                "slotId" to session.slotId,
                "hostUserId" to session.hostUserId,
                "guestUserId" to session.guestUserId,
                "channelName" to session.channelName,
                "startTime" to session.startTime,
                "endTime" to session.endTime,
                "status" to session.status.name
            )
            
            val docRef = callsCollection.add(sessionData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCallSession(slotId: String): Result<VideoCallSession?> {
        return try {
            val snapshot = callsCollection
                .whereEqualTo("slotId", slotId)
                .whereEqualTo("status", CallStatus.ACTIVE.name)
                .get()
                .await()
            
            val session = snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(VideoCallSession::class.java)?.copy(id = doc.id)
            }
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCallStatus(sessionId: String, status: CallStatus): Result<Boolean> {
        return try {
            callsCollection.document(sessionId)
                .update("status", status.name)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun endCallSession(sessionId: String): Result<Boolean> {
        return try {
            val updates = hashMapOf<String, Any>(
                "status" to CallStatus.ENDED.name,
                "endTime" to System.currentTimeMillis()
            )
            
            callsCollection.document(sessionId)
                .update(updates)
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Listen for incoming calls for a specific user (mentor)
    fun observeIncomingCalls(userId: String, onCallsReceived: (List<VideoCallSession>) -> Unit) {
        callsCollection
            .whereEqualTo("guestUserId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onCallsReceived(emptyList())
                    return@addSnapshotListener
                }
                
                // Filter ACTIVE calls in memory instead of Firestore query
                val sessions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(VideoCallSession::class.java)?.copy(id = doc.id)
                }?.filter { it.status == CallStatus.ACTIVE } ?: emptyList()
                
                onCallsReceived(sessions)
            }
    }
    
    // Get a specific session by ID
    suspend fun getSessionById(sessionId: String): Result<VideoCallSession?> {
        return try {
            val doc = callsCollection.document(sessionId).get().await()
            val session = doc.toObject(VideoCallSession::class.java)?.copy(id = doc.id)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
