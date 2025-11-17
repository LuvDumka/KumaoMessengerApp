package com.example.mentorconnect.data.local

import com.example.mentorconnect.data.model.Mentor

/**
 * Local mentor repository - uses in-memory storage
 */
class LocalMentorRepository {
    
    suspend fun getMentorsOnce(): List<Mentor> {
        return LocalDataStore.getAllMentors()
    }
}
