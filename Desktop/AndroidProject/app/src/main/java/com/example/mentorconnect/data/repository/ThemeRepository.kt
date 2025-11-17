package com.example.mentorconnect.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeRepository(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _isDarkTheme = MutableStateFlow(preferences.getBoolean(KEY_DARK_THEME, false))

    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
        _isDarkTheme.value = enabled
    }

    companion object {
        private const val PREFS_NAME = "mentor_connect_prefs"
        private const val KEY_DARK_THEME = "key_dark_theme"
    }
}
