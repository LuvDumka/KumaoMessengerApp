package com.example.mentorconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.mentorconnect.R
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.databinding.ActivitySignupBinding
import com.example.mentorconnect.ui.helpbot.HelpMentorSelectionActivity
import com.example.mentorconnect.ui.mentor.MentorProfileSetupActivity
import com.example.mentorconnect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory(ServiceLocator.provideAuthRepository())
    }
    private var selectedRole: String = "STUDENT"
    private var pendingRole: String = "STUDENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up role toggle
        binding.toggleGroupRole.check(binding.buttonStudent.id)
        binding.toggleGroupRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedRole = when (checkedId) {
                    binding.buttonMentor.id -> "MENTOR"
                    else -> "STUDENT"
                }
                updateRoleUi()
            }
        }

        updateRoleUi()

        binding.buttonSignUp.setOnClickListener {
            val name = binding.editName.text?.toString().orEmpty()
            val email = binding.editEmail.text?.toString().orEmpty()
            val password = binding.editPassword.text?.toString().orEmpty()
            val education = binding.editEducation.text?.toString().orEmpty()
            val interest = binding.editInterest.text?.toString().orEmpty()
            val mentorExpertise = binding.editMentorExpertise.text?.toString().orEmpty()
            val mentorExperience = binding.editMentorExperience.text?.toString().orEmpty()
            val mentorRate = binding.editMentorRate.text?.toString().orEmpty()

            if (selectedRole == "STUDENT") {
                if (education.isBlank() || interest.isBlank()) {
                    Toast.makeText(this, "Please share your education and interest area", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                if (mentorExpertise.isBlank() || mentorExperience.isBlank() || mentorRate.isBlank()) {
                    Toast.makeText(this, "Please fill mentor expertise, experience, and pricing", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            pendingRole = selectedRole

            viewModel.signup(
                name,
                email,
                password,
                education,
                interest,
                mentorExpertise,
                mentorExperience,
                mentorRate,
                selectedRole
            )
        }

        binding.textLogin.setOnClickListener {
            finish()
        }

        observeState()
    }

    private fun observeState() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> {
                    setLoading(false)
                    // Route based on role
                    if (pendingRole == "MENTOR") {
                        navigateToMentorProfileSetup()
                    } else {
                        navigateToStudentHome()
                    }
                    viewModel.resetState()
                }
                is Resource.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                Resource.Idle -> setLoading(false)
            }
        }
    }

    private fun navigateToStudentHome() {
        val intent = Intent(this, HelpMentorSelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToMentorProfileSetup() {
        val intent = Intent(this, MentorProfileSetupActivity::class.java).apply {
            putExtra(MentorProfileSetupActivity.EXTRA_PREFILL_EXPERTISE,
                binding.editMentorExpertise.text?.toString().orEmpty())
            putExtra(MentorProfileSetupActivity.EXTRA_PREFILL_EXPERIENCE,
                binding.editMentorExperience.text?.toString().orEmpty())
            putExtra(MentorProfileSetupActivity.EXTRA_PREFILL_RATE,
                binding.editMentorRate.text?.toString().orEmpty())
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun updateRoleUi() {
        val isStudent = selectedRole == "STUDENT"
        binding.studentFieldsGroup.isVisible = isStudent
        binding.mentorFieldsGroup.isVisible = !isStudent
        binding.textRoleDescription.setText(
            if (isStudent) R.string.role_student_description else R.string.role_mentor_description
        )
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSignUp.isEnabled = !isLoading
    }
}
