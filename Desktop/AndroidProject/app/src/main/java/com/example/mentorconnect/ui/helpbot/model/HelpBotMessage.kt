package com.example.mentorconnect.ui.helpbot.model

import androidx.annotation.Keep

@Keep
data class HelpBotMessage(
    val id: Long,
    val text: String,
    val isBot: Boolean
)
