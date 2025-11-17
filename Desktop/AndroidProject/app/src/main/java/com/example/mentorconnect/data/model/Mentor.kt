package com.example.mentorconnect.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mentor(
    val id: String = "",
    val name: String = "",
    val expertise: String = "",
    val bio: String = "",
    val avatarUrl: String? = null,
    val experience: String = "5+ years",
    val rating: Double = 4.5,
    val hourlyRate: Int = 50,
    val skills: List<String> = emptyList()
) : Parcelable
