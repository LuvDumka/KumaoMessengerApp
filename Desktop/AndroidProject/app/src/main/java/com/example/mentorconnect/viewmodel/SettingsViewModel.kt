package com.example.mentorconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.mentorconnect.data.repository.AuthRepository
import com.example.mentorconnect.data.repository.ThemeRepository

class SettingsViewModel(
    private val themeRepository: ThemeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isDarkTheme = themeRepository.isDarkTheme.asLiveData()

    fun updateTheme(enabled: Boolean) {
        themeRepository.setDarkTheme(enabled)
    }

    fun logout() {
        authRepository.logout()
    }

    class Factory(
        private val themeRepository: ThemeRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SettingsViewModel::class.java) { "Unknown ViewModel class" }
            return SettingsViewModel(themeRepository, authRepository) as T
        }
    }
}
