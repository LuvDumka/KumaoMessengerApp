package com.example.mentorconnect.core

import android.content.Context
import com.example.mentorconnect.data.local.LocalAuthRepository
import com.example.mentorconnect.data.local.LocalBookingRepository
import com.example.mentorconnect.data.local.LocalChatRepository
import com.example.mentorconnect.data.local.LocalDataStore
import com.example.mentorconnect.data.local.LocalMentorRepository
import com.example.mentorconnect.data.local.LocalUser
import com.example.mentorconnect.data.local.LocalUserRepository
import com.example.mentorconnect.data.repository.AuthRepository
import com.example.mentorconnect.data.repository.BookingRepository
import com.example.mentorconnect.data.repository.ChatRepository
import com.example.mentorconnect.data.repository.MentorProfileRepository
import com.example.mentorconnect.data.repository.MentorRepository
import com.example.mentorconnect.data.repository.ThemeRepository
import com.example.mentorconnect.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object ServiceLocator {
    private lateinit var appContext: Context

    // Toggle to use local storage (in-memory) instead of Firebase
    // Set to true to use local storage (no Firebase needed)
    // Set to false to use Firebase (default for now until integration is complete)
    private const val USE_LOCAL_STORAGE = false

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val themeRepository: ThemeRepository by lazy { ThemeRepository(appContext) }
    
    // Firebase repositories
    private val firebaseMentorRepository: MentorRepository by lazy { MentorRepository() }
    private val firebaseMentorProfileRepository: MentorProfileRepository by lazy { MentorProfileRepository(firestore) }
    private val firebaseAuthRepository: AuthRepository by lazy { AuthRepository(firebaseAuth, firestore) }
    private val firebaseUserRepository: UserRepository by lazy { UserRepository(firestore) }
    private val firebaseBookingRepository: BookingRepository by lazy { BookingRepository() }
    private val firebaseChatRepository: ChatRepository by lazy { ChatRepository() }
    
    // Local repositories (in-memory storage)
    private val localAuthRepository: LocalAuthRepository by lazy { LocalAuthRepository() }
    private val localUserRepository: LocalUserRepository by lazy { LocalUserRepository() }
    private val localMentorRepository: LocalMentorRepository by lazy { LocalMentorRepository() }
    private val localBookingRepository: LocalBookingRepository by lazy { LocalBookingRepository() }
    private val localChatRepository: LocalChatRepository by lazy { LocalChatRepository() }

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun provideAuthRepository(): AuthRepository = firebaseAuthRepository

    fun provideUserRepository(): UserRepository = firebaseUserRepository

    fun provideMentorRepository(): MentorRepository = firebaseMentorRepository

    fun provideMentorProfileRepository(): MentorProfileRepository = firebaseMentorProfileRepository
    
    fun provideBookingRepository(): BookingRepository = firebaseBookingRepository
    
    fun provideChatRepository(): ChatRepository = firebaseChatRepository

    fun provideThemeRepository(): ThemeRepository = themeRepository
    
    // For future local storage integration (when USE_LOCAL_STORAGE = true):
    // Update these methods to return common interfaces or handle both types
    
    /**
     * Get current user - works for both Firebase and Local storage
     */
    fun getCurrentUser(): Any? {
        return if (USE_LOCAL_STORAGE) {
            val userId = LocalDataStore.currentUserId
            val userEmail = LocalDataStore.currentUserEmail
            if (userId != null && userEmail != null) {
                LocalUser(userId, userEmail)
            } else null
        } else {
            firebaseAuth.currentUser
        }
    }
    
    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return if (USE_LOCAL_STORAGE) {
            LocalDataStore.currentUserId != null
        } else {
            firebaseAuth.currentUser != null
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        if (USE_LOCAL_STORAGE) {
            localAuthRepository.logout()
        } else {
            firebaseAuthRepository.logout()
        }
    }
}
