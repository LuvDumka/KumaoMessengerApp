package com.example.mentorconnect.data.model

data class MentorProfile(
    val mentorId: String = "",
    val name: String = "",
    val email: String = "",
    val expertise: String = "", // e.g., "Data Science", "Web Development"
    val bio: String = "",
    val experience: String = "", // e.g., "5 years"
    val hourlyRate: Double = 0.0,
    val availability: String = "", // e.g., "Weekdays 9AM-5PM"
    val skills: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
    val photoUrl: String = "",
    val rating: Double = 0.0,
    val totalSessions: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
