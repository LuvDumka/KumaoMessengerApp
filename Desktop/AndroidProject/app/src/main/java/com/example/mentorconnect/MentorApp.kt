package com.example.mentorconnect

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.mentorconnect.core.ServiceLocator
import com.google.firebase.FirebaseApp

class MentorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            
            // Initialize ServiceLocator
            ServiceLocator.initialize(this)
            
            // Apply saved theme preference
            val themeRepository = ServiceLocator.provideThemeRepository()
            val nightMode = if (themeRepository.isDarkTheme.value) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to system default if initialization fails
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
