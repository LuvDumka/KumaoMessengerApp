package com.example.mentorconnect.data.repository

import com.example.mentorconnect.data.model.UserProfile
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore
) {

    suspend fun getUserProfile(uid: String): Resource<UserProfile> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            val profile = snapshot.toObject(UserProfile::class.java)
            if (profile != null) {
                Resource.Success(profile.copy(uid = uid))
            } else {
                Resource.Error("Profile not found")
            }
        } catch (exception: Exception) {
            Resource.Error(exception.localizedMessage ?: "Unable to load profile")
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}
