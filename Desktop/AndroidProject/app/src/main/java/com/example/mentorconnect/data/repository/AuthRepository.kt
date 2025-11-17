package com.example.mentorconnect.data.repository

import com.example.mentorconnect.data.model.UserProfile
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun login(email: String, password: String): Resource<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(Unit)
        } catch (exception: Exception) {
            Resource.Error(exception.localizedMessage ?: "Unable to complete login")
        }
    }

    suspend fun signup(
        name: String,
        email: String,
        password: String,
        studentEducation: String,
        studentInterest: String,
        mentorExpertise: String,
        mentorExperience: String,
        mentorRate: String,
        role: String = "STUDENT"
    ): Resource<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Resource.Error("User ID missing")

            val profile = UserProfile(
                uid = uid,
                name = name,
                email = email,
                education = if (role == "STUDENT") studentEducation else "",
                interest = if (role == "STUDENT") studentInterest else "",
                mentorExpertise = if (role == "MENTOR") mentorExpertise else "",
                mentorExperience = if (role == "MENTOR") mentorExperience else "",
                mentorRate = if (role == "MENTOR") mentorRate else "",
                role = role,
                profileCompleted = role == "STUDENT" // Students don't need additional profile setup
            )

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .set(profile)
                .await()

            Resource.Success(Unit)
        } catch (exception: Exception) {
            Resource.Error(exception.localizedMessage ?: "Unable to sign up")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun currentUser() = firebaseAuth.currentUser

    companion object {
        private const val USERS_COLLECTION = "users"
    }
}
