package com.example.mentorconnect.ui.mentor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.data.model.MentorProfile
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.databinding.ActivityMentorProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MentorProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMentorProfileSetupBinding
    private val repository by lazy { ServiceLocator.provideMentorProfileRepository() }
    private val userRepository by lazy { ServiceLocator.provideUserRepository() }
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMentorProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefillFromExtras()

        binding.buttonSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun prefillFromExtras() {
        binding.editExpertise.setText(intent.getStringExtra(EXTRA_PREFILL_EXPERTISE).orEmpty())
        binding.editExperience.setText(intent.getStringExtra(EXTRA_PREFILL_EXPERIENCE).orEmpty())
        binding.editHourlyRate.setText(intent.getStringExtra(EXTRA_PREFILL_RATE).orEmpty())
    }

    private fun saveProfile() {
        val expertise = binding.editExpertise.text?.toString().orEmpty().trim()
        val bio = binding.editBio.text?.toString().orEmpty().trim()
        val experience = binding.editExperience.text?.toString().orEmpty().trim()
        val hourlyRateStr = binding.editHourlyRate.text?.toString().orEmpty().trim()
        val availability = binding.editAvailability.text?.toString().orEmpty().trim()
        val skillsStr = binding.editSkills.text?.toString().orEmpty().trim()
        val languagesStr = binding.editLanguages.text?.toString().orEmpty().trim()

        // Validation
        if (expertise.isBlank() || bio.isBlank() || experience.isBlank() || 
            hourlyRateStr.isBlank() || availability.isBlank()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val hourlyRate = hourlyRateStr.toDoubleOrNull()
        if (hourlyRate == null || hourlyRate <= 0) {
            Toast.makeText(this, "Please enter a valid hourly rate", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            // Get user profile for name and email
            val userResult = userRepository.getUserProfile(currentUser.uid)
            
            if (userResult is Resource.Success) {
                val userProfile = userResult.data
                
                val mentorProfile = MentorProfile(
                    mentorId = currentUser.uid,
                    name = userProfile.name,
                    email = userProfile.email,
                    expertise = expertise,
                    bio = bio,
                    experience = experience,
                    hourlyRate = hourlyRate,
                    availability = availability,
                    skills = skillsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    languages = languagesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                )

                val result = repository.saveMentorProfile(mentorProfile)

                runOnUiThread {
                    setLoading(false)
                    when (result) {
                        is Resource.Success -> {
                            Toast.makeText(this@MentorProfileSetupActivity, 
                                "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                            navigateToMentorDashboard()
                        }
                        is Resource.Error -> {
                            Toast.makeText(this@MentorProfileSetupActivity, 
                                result.message, Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            } else {
                runOnUiThread {
                    setLoading(false)
                    Toast.makeText(this@MentorProfileSetupActivity, 
                        "Failed to load user profile", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun navigateToMentorDashboard() {
        val intent = Intent(this, MentorDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSaveProfile.isEnabled = !isLoading
    }

    companion object {
        const val EXTRA_PREFILL_EXPERTISE = "extra_prefill_expertise"
        const val EXTRA_PREFILL_EXPERIENCE = "extra_prefill_experience"
        const val EXTRA_PREFILL_RATE = "extra_prefill_rate"
    }
}
