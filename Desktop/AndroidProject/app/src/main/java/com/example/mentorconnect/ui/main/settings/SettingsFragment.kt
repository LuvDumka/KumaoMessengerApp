package com.example.mentorconnect.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mentorconnect.R
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.databinding.FragmentSettingsBinding
import com.example.mentorconnect.ui.auth.LoginActivity
import com.example.mentorconnect.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SettingsViewModel
    private var suppressThemeListener = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            // Initialize ViewModel with proper error handling
            val themeRepo = ServiceLocator.provideThemeRepository()
            val authRepo = ServiceLocator.provideAuthRepository()
            val factory = SettingsViewModel.Factory(themeRepo, authRepo)
            viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
            
            setupActions()
            observeTheme()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: setup without ViewModel
            setupFallbackUI()
        }
    }

    private fun observeTheme() {
        try {
            viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDark ->
                if (isDark != null) {
                    suppressThemeListener = true
                    updateThemeUI(isDark)
                    suppressThemeListener = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If observation fails, try to get current theme from system
            val currentNightMode = resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
            val isDarkMode = currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            updateThemeUI(isDarkMode)
        }
    }

    private fun updateThemeUI(isDark: Boolean) {
        try {
            if (isDark) {
                binding.radioDarkTheme.isChecked = true
            } else {
                binding.radioLightTheme.isChecked = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupActions() {
        try {
            // Theme selection via radio buttons
            binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
                if (suppressThemeListener) return@setOnCheckedChangeListener
                
                val isDarkMode = when (checkedId) {
                    R.id.radioDarkTheme -> true
                    R.id.radioLightTheme -> false
                    else -> return@setOnCheckedChangeListener
                }
                
                try {
                    viewModel.updateTheme(isDarkMode)
                    applyTheme(isDarkMode)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Still apply theme even if save fails
                    applyTheme(isDarkMode)
                }
            }

            // Logout button
            binding.buttonLogout.setOnClickListener {
                try {
                    viewModel.logout()
                    navigateToLogin()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Try to navigate anyway
                    navigateToLogin()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupFallbackUI() {
        try {
            // Basic functionality without ViewModel
            val currentNightMode = resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
            val isDarkMode = currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            updateThemeUI(isDarkMode)
            
            binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
                try {
                    val isDark = checkedId == R.id.radioDarkTheme
                    applyTheme(isDark)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            binding.buttonLogout.setOnClickListener {
                try {
                    ServiceLocator.provideAuthRepository().logout()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                navigateToLogin()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
