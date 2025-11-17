package com.example.mentorconnect.data.local

import com.example.mentorconnect.data.model.UserProfile
import com.example.mentorconnect.data.util.Resource

/**
 * Local user repository - uses in-memory storage
 */
class LocalUserRepository {
    
    suspend fun getUserProfile(uid: String): Resource<UserProfile> {
        return try {
            val profile = LocalDataStore.getUser(uid)
            if (profile != null) {
                Resource.Success(profile)
            } else {
                Resource.Error("Profile not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load profile")
        }
    }
}
