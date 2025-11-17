package com.example.mentorconnect.data.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val education: String = "",
    val interest: String = "",
    val mentorExpertise: String = "",
    val mentorExperience: String = "",
    val mentorRate: String = "",
    val role: String = "STUDENT", // "STUDENT" or "MENTOR"
    val profileCompleted: Boolean = false // Track if mentor has completed profile setup
)
