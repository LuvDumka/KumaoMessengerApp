package com.example.mentorconnect.data.repository

import com.example.mentorconnect.data.model.Mentor
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MentorRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val mentorsCollection = firestore.collection("mentors")

    fun listenToMentors(): Flow<List<Mentor>> = callbackFlow {
        val listener = mentorsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList()).isSuccess
                return@addSnapshotListener
            }
            val mentors = snapshot?.documents?.mapNotNull { it.toMentor() } ?: emptyList()
            trySend(mentors).isSuccess
        }
        awaitClose { listener.remove() }
    }

    suspend fun getMentorsOnce(): List<Mentor> {
        val snapshot = mentorsCollection.get().await()
        return snapshot.documents.mapNotNull { it.toMentor() }
    }

    private fun DocumentSnapshot.toMentor(): Mentor? {
        val name = getString("name") ?: return null

        val ratingValue = when (val rawRating = get("rating")) {
            is Number -> rawRating.toDouble()
            is String -> rawRating.toDoubleOrNull()
            else -> null
        } ?: 4.5

        val hourlyRateValue = when (val rawRate = get("hourlyRate")) {
            is Number -> rawRate.toDouble()
            is String -> rawRate.toDoubleOrNull()
            else -> null
        } ?: 50.0

        return Mentor(
            id = id,
            name = name,
            expertise = getString("expertise") ?: "",
            bio = getString("bio") ?: "",
            avatarUrl = getString("avatarUrl"),
            experience = getString("experience") ?: "5+ years",
            rating = ratingValue,
            hourlyRate = hourlyRateValue.toInt(),
            skills = (get("skills") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    }
}
