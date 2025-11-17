package com.example.mentorconnect.data.repository

import com.example.mentorconnect.data.model.MentorProfile
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class MentorProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveMentorProfile(profile: MentorProfile): Resource<Unit> {
        return try {
            writeMentorProfile(profile)
            markProfileCompleted(profile.mentorId)
            Resource.Success(Unit)
        } catch (firestoreException: FirebaseFirestoreException) {
            if (firestoreException.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                try {
                    writeMentorProfileFallback(profile)
                    markProfileCompleted(profile.mentorId)
                    Resource.Success(Unit)
                } catch (fallbackException: Exception) {
                    Resource.Error(fallbackException.localizedMessage ?: "Failed to save profile")
                }
            } else {
                Resource.Error(firestoreException.localizedMessage ?: "Failed to save profile")
            }
        } catch (exception: Exception) {
            Resource.Error(exception.localizedMessage ?: "Failed to save profile")
        }
    }

    suspend fun getMentorProfile(mentorId: String): Resource<MentorProfile> {
        return try {
            val profile = fetchMentorProfile(mentorId)
            Resource.Success(profile)
        } catch (notFound: IllegalStateException) {
            try {
                val fallbackProfile = fetchMentorProfileFallback(mentorId)
                Resource.Success(fallbackProfile)
            } catch (fallbackException: Exception) {
                Resource.Error(fallbackException.localizedMessage ?: "Failed to fetch profile")
            }
        } catch (firestoreException: FirebaseFirestoreException) {
            if (firestoreException.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                try {
                    val fallbackProfile = fetchMentorProfileFallback(mentorId)
                    Resource.Success(fallbackProfile)
                } catch (fallbackException: Exception) {
                    Resource.Error(fallbackException.localizedMessage ?: "Failed to fetch profile")
                }
            } else {
                Resource.Error(firestoreException.localizedMessage ?: "Failed to fetch profile")
            }
        } catch (exception: Exception) {
            Resource.Error(exception.localizedMessage ?: "Failed to fetch profile")
        }
    }

    suspend fun updateMentorProfile(mentorId: String, updates: Map<String, Any>): Resource<Unit> {
        return try {
            firestore.collection(MENTORS_COLLECTION)
                .document(mentorId)
                .update(updates)
                .await()

            Resource.Success(Unit)
        } catch (firestoreException: FirebaseFirestoreException) {
            if (firestoreException.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                try {
                    userScopedMentorDoc(mentorId)
                        .update(updates)
                        .await()
                    Resource.Success(Unit)
                } catch (fallbackException: Exception) {
                    Resource.Error(fallbackException.localizedMessage ?: "Failed to update profile")
                }
            } else {
                Resource.Error(firestoreException.localizedMessage ?: "Failed to update profile")
            }
        } catch (exception: Exception) {
            Resource.Error(exception.localizedMessage ?: "Failed to update profile")
        }
    }

    companion object {
        private const val MENTORS_COLLECTION = "mentors"
        private const val USERS_COLLECTION = "users"
        private const val USER_MENTOR_SUBCOLLECTION = "mentorProfile"
        private const val USER_MENTOR_DOC = "details"
    }

    private fun mentorDoc(mentorId: String) =
        firestore.collection(MENTORS_COLLECTION).document(mentorId)

    private fun userScopedMentorDoc(mentorId: String) =
        firestore.collection(USERS_COLLECTION)
            .document(mentorId)
            .collection(USER_MENTOR_SUBCOLLECTION)
            .document(USER_MENTOR_DOC)

    private suspend fun writeMentorProfile(profile: MentorProfile) {
        mentorDoc(profile.mentorId).set(profile).await()
    }

    private suspend fun writeMentorProfileFallback(profile: MentorProfile) {
        userScopedMentorDoc(profile.mentorId).set(profile).await()
    }

    private suspend fun fetchMentorProfile(mentorId: String): MentorProfile {
        val snapshot = mentorDoc(mentorId).get().await()
        if (!snapshot.exists()) throw IllegalStateException("Mentor profile not found")
        return snapshot.toObject(MentorProfile::class.java)
            ?: throw IllegalStateException("Failed to parse profile")
    }

    private suspend fun fetchMentorProfileFallback(mentorId: String): MentorProfile {
        val snapshot = userScopedMentorDoc(mentorId).get().await()
        if (!snapshot.exists()) throw IllegalStateException("Mentor profile not found")
        return snapshot.toObject(MentorProfile::class.java)
            ?: throw IllegalStateException("Failed to parse profile")
    }

    private suspend fun markProfileCompleted(mentorId: String) {
        firestore.collection(USERS_COLLECTION)
            .document(mentorId)
            .update("profileCompleted", true)
            .await()
    }
}
