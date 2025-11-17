package com.example.mentorconnect.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mentorconnect.R
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.databinding.ActivityLoginBinding
import com.example.mentorconnect.ui.helpbot.HelpMentorSelectionActivity
import com.example.mentorconnect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory(ServiceLocator.provideAuthRepository())
    }
    private var selectedRole: String = "STUDENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up role toggle
        binding.toggleGroupRole.check(binding.buttonStudent.id)
        binding.toggleGroupRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedRole = when (checkedId) {
                    binding.buttonMentor.id -> "MENTOR"
                    else -> "STUDENT"
                }
                updateRoleDescription()
            }
        }

        updateRoleDescription()

        binding.buttonLogin.setOnClickListener {
            val email = binding.editEmail.text?.toString().orEmpty()
            val password = binding.editPassword.text?.toString().orEmpty()
            viewModel.login(email, password)
        }

        binding.textSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        observeState()
    }

    private fun observeState() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> {
                    setLoading(false)
                    checkUserRoleAndNavigate()
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

    private fun checkUserRoleAndNavigate() {
        val userRepository = ServiceLocator.provideUserRepository()
        val currentUser = ServiceLocator.provideAuthRepository().currentUser()

        if (currentUser == null) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch user profile to check role
        lifecycleScope.launch {
            val result = userRepository.getUserProfile(currentUser.uid)
            runOnUiThread {
                when (result) {
                    is Resource.Success -> {
                        val userProfile = result.data
                        when (userProfile.role) {
                            "MENTOR" -> {
                                if (userProfile.profileCompleted) {
                                    navigateToMentorDashboard()
                                } else {
                                    navigateToMentorProfileSetup()
                                }
                            }
                            else -> navigateToStudentHome()
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@LoginActivity, 
                            "Failed to load profile: ${result.message}", 
                            Toast.LENGTH_LONG).show()
                        // Default to student dashboard
                        navigateToStudentHome()
                    }
                    else -> navigateToStudentHome()
                }
            }
        }
    }

    private fun navigateToStudentHome() {
        val intent = Intent(this, HelpMentorSelectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToMentorDashboard() {
        val intent = Intent(this, com.example.mentorconnect.ui.mentor.MentorDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToMentorProfileSetup() {
        val intent = Intent(this, com.example.mentorconnect.ui.mentor.MentorProfileSetupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonLogin.isEnabled = !isLoading
    }

    private fun updateRoleDescription() {
        val descriptionRes = if (selectedRole == "STUDENT") {
            R.string.role_student_description
        } else {
            R.string.role_mentor_description
        }
        binding.textRoleDescription.setText(descriptionRes)
    }
}
