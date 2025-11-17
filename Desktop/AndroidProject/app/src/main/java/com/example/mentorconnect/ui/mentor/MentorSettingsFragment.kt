package com.example.mentorconnect.ui.mentor

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
import com.example.mentorconnect.databinding.FragmentMentorSettingsBinding
import com.example.mentorconnect.ui.auth.LoginActivity
import com.example.mentorconnect.viewmodel.SettingsViewModel

class MentorSettingsFragment : Fragment() {

    private var _binding: FragmentMentorSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMentorSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            viewModel = ViewModelProvider(
                this,
                SettingsViewModel.Factory(
                    ServiceLocator.provideThemeRepository(),
                    ServiceLocator.provideAuthRepository() as com.example.mentorconnect.data.repository.AuthRepository
                )
            )[SettingsViewModel::class.java]

            setupViews()
            observeTheme()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback if ViewModel fails
            setupFallbackUI()
        }
    }

    private fun setupViews() {
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val isDark = checkedId == R.id.radioDark
            viewModel.updateTheme(isDark)
            applyTheme(isDark)
        }

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
            navigateToLogin()
        }
    }

    private fun observeTheme() {
        try {
            viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDark ->
                if (isDark != null) {
                    binding.radioGroupTheme.check(
                        if (isDark) R.id.radioDark else R.id.radioLight
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If observation fails, use system theme
            val currentNightMode = resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
            val isDarkMode = currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            binding.radioGroupTheme.check(
                if (isDarkMode) R.id.radioDark else R.id.radioLight
            )
        }
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupFallbackUI() {
        binding.radioGroupTheme.check(R.id.radioLight)
        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            applyTheme(checkedId == R.id.radioDark)
        }
        binding.buttonLogout.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
