package com.example.mentorconnect.data.local

import com.example.mentorconnect.data.model.UserProfile
import com.example.mentorconnect.data.util.Resource

/**
 * Local auth repository - no Firebase, uses in-memory storage
 */
class LocalAuthRepository {
    
    suspend fun login(email: String, password: String): Resource<Unit> {
        return try {
            val user = LocalDataStore.getUserByEmail(email)
            if (user != null) {
                // Simple password check (in real app, never store plain passwords!)
                val expectedPassword = when (email) {
                    "jane@example.com", "rahul@example.com" -> "Mentor@123"
                    "steve@example.com", "ayesha@example.com" -> "Student@123"
                    else -> "password"
                }
                
                if (password == expectedPassword) {
                    LocalDataStore.currentUserId = user.uid
                    LocalDataStore.currentUserEmail = user.email
                    Resource.Success(Unit)
                } else {
                    Resource.Error("Invalid password")
                }
            } else {
                Resource.Error("User not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Login failed")
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
            // Check if user already exists
            if (LocalDataStore.getUserByEmail(email) != null) {
                return Resource.Error("Email already registered")
            }
            
            val uid = "user_${System.currentTimeMillis()}"
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
                profileCompleted = role == "STUDENT"
            )
            
            LocalDataStore.saveUser(profile)
            LocalDataStore.currentUserId = uid
            LocalDataStore.currentUserEmail = email
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Signup failed")
        }
    }
    
    fun logout() {
        LocalDataStore.currentUserId = null
        LocalDataStore.currentUserEmail = null
    }
    
    fun currentUser(): LocalUser? {
        val uid = LocalDataStore.currentUserId ?: return null
        val email = LocalDataStore.currentUserEmail ?: return null
        return LocalUser(uid, email)
    }
}

data class LocalUser(
    val uid: String,
    val email: String
)
