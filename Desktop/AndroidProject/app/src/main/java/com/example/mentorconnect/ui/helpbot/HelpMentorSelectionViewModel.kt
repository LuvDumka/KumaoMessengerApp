package com.example.mentorconnect.ui.helpbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.Mentor
import com.example.mentorconnect.data.repository.MentorRepository
import com.example.mentorconnect.ui.helpbot.model.HelpBotMessage
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HelpMentorSelectionViewModel(
    private val mentorRepository: MentorRepository = MentorRepository()
) : ViewModel() {

    private val conversation = mutableListOf<HelpBotMessage>()

    private val _messages = MutableLiveData<List<HelpBotMessage>>()
    val messages: LiveData<List<HelpBotMessage>> = _messages

    private val _canFinish = MutableLiveData(false)
    val canFinish: LiveData<Boolean> = _canFinish

    private val _recommendations = MutableLiveData<List<Mentor>>(emptyList())
    val recommendations: LiveData<List<Mentor>> = _recommendations

    private var desiredSkills: List<String> = emptyList()
    private var minBudget: Int? = null
    private var maxBudget: Int? = null
    private var experiencePreference: String? = null
    private var phase: ConversationPhase = ConversationPhase.ASK_SKILLS

    init {
        addBotMessage("Hi, I'm helpMentorSelection. What skills or topics are you hoping to focus on?")
    }

    fun sendUserMessage(input: String) {
        val text = input.trim()
        if (text.isEmpty()) return

        appendMessage(HelpBotMessage(System.currentTimeMillis(), text, false))
        when (phase) {
            ConversationPhase.ASK_SKILLS -> handleSkillsPhase(text)
            ConversationPhase.ASK_BUDGET -> handleBudgetPhase(text)
            ConversationPhase.ASK_EXPERIENCE -> handleExperiencePhase(text)
            ConversationPhase.RECOMMENDATIONS -> addBotMessage("Feel free to tap Go to mentors whenever you're ready!")
        }
    }

    fun skipToRecommendations() {
        if (phase == ConversationPhase.RECOMMENDATIONS) return
        addBotMessage("No worries! I'll show a few popular mentors you can explore right away.")
        desiredSkills = emptyList()
        minBudget = null
        maxBudget = null
        experiencePreference = null
        shareRecommendations()
    }

    private fun handleSkillsPhase(text: String) {
        val skills = parseSkills(text)
        desiredSkills = skills
        phase = ConversationPhase.ASK_BUDGET
        val skillSummary = if (skills.isEmpty()) "those general goals" else skills.joinToString()
        addBotMessage("Got it, I'll look for mentors who can help with $skillSummary. What's your hourly budget range?")
    }

    private fun handleBudgetPhase(text: String) {
        val numbers = Regex("\\d+").findAll(text).map { it.value.toInt() }.sorted().toList()
        if (numbers.isEmpty()) {
            addBotMessage("Could you share a rough budget? Even a single number helps me narrow options.")
            return
        }
        minBudget = numbers.first()
        maxBudget = numbers.last()
        phase = ConversationPhase.ASK_EXPERIENCE
        addBotMessage("Thanks! Do you prefer mentors with a specific experience level (e.g., senior, 5+ years)?")
    }

    private fun handleExperiencePhase(text: String) {
        experiencePreference = text
        shareRecommendations()
    }

    private fun shareRecommendations() {
        phase = ConversationPhase.RECOMMENDATIONS
        viewModelScope.launch {
            // tiny delay for more natural feel
            delay(350)
            val mentors = try {
                mentorRepository.getMentorsOnce()
            } catch (e: Exception) {
                emptyList()
            }
            val filtered = mentors.filter { matchesSkill(it) && matchesBudget(it) && matchesExperience(it) }
            val ranked = (if (filtered.isEmpty()) mentors else filtered)
                .sortedBy { recommendationScore(it) }
                .take(3)

            _recommendations.value = ranked
            _canFinish.value = true
            addBotMessage(buildRecommendationMessage(ranked, mentors.isEmpty() || filtered.isEmpty()))
            addBotMessage("Tap Go to mentors to explore their profiles or keep chatting if you want a different focus.")
        }
    }

    private fun matchesSkill(mentor: Mentor): Boolean {
        if (desiredSkills.isEmpty()) return true
        val mentorSkills = mentor.skills.map { it.lowercase(Locale.getDefault()) }
        return desiredSkills.any { skill ->
            mentorSkills.any { mentorSkill -> mentorSkill.contains(skill) }
        }
    }

    private fun matchesBudget(mentor: Mentor): Boolean {
        if (minBudget == null && maxBudget == null) return true
        val min = minBudget ?: mentor.hourlyRate
        val max = maxBudget ?: mentor.hourlyRate
        return mentor.hourlyRate in min..max
    }

    private fun matchesExperience(mentor: Mentor): Boolean {
        val preference = experiencePreference?.lowercase(Locale.getDefault()) ?: return true
        if (preference.isBlank()) return true
        val mentorExperience = mentor.experience.lowercase(Locale.getDefault())
        return mentorExperience.contains(preference) ||
            Regex("\\d+").find(mentorExperience)?.value == Regex("\\d+").find(preference)?.value
    }

    private fun recommendationScore(mentor: Mentor): Int {
        val budgetTarget = if (minBudget != null && maxBudget != null) (minBudget!! + maxBudget!!) / 2 else mentor.hourlyRate
        val budgetDelta = abs(mentor.hourlyRate - budgetTarget)
        val skillHits = desiredSkills.count { desired ->
            mentor.skills.any { skill -> skill.contains(desired, ignoreCase = true) }
        }
        return budgetDelta - (skillHits * 5)
    }

    private fun buildRecommendationMessage(mentors: List<Mentor>, usedFallback: Boolean): String {
        if (mentors.isEmpty()) return "I couldn't find any mentors just yet. Try adjusting your preferences."
        val header = if (usedFallback) {
            "I couldn't find an exact match, but these mentors are popular right now:\n"
        } else {
            "Here are the mentors that best match what you described:\n"
        }
        val list = mentors.joinToString(separator = "\n") { mentor ->
            "• ${mentor.name} — ${mentor.expertise} | ${mentor.experience} | $${mentor.hourlyRate}/h"
        }
        return header + list
    }

    private fun parseSkills(text: String): List<String> {
        if (text.isBlank()) return emptyList()
        return text.lowercase(Locale.getDefault())
            .replace(" and ", ",")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun addBotMessage(message: String) {
        appendMessage(HelpBotMessage(System.currentTimeMillis(), message, true))
    }

    private fun appendMessage(message: HelpBotMessage) {
        conversation.add(message)
        _messages.value = conversation.toList()
    }

    private enum class ConversationPhase {
        ASK_SKILLS,
        ASK_BUDGET,
        ASK_EXPERIENCE,
        RECOMMENDATIONS
    }
}
