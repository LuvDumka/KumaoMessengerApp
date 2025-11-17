package com.example.mentorconnect.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mentorconnect.R
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.databinding.FragmentProfileBinding
import com.example.mentorconnect.viewmodel.ProfileViewModel
import com.example.mentorconnect.util.AvatarGenerator

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.Factory(
            ServiceLocator.provideUserRepository(),
            ServiceLocator.provideAuthRepository()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshProfile()
    }

    private fun observeProfile() {
        viewModel.profileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> {
                    setLoading(false)
                    val profile = state.data
                    binding.textProfileName.text = profile.name
                    binding.textProfileEmail.text = profile.email
                    val isMentor = profile.role.equals("MENTOR", ignoreCase = true)
                    binding.studentInfoGroup.isVisible = !isMentor
                    binding.mentorInfoGroup.isVisible = isMentor

                    binding.textEducation.text = profile.education.ifBlank { getString(R.string.placeholder_not_updated) }
                    binding.textInterest.text = profile.interest.ifBlank { getString(R.string.placeholder_not_updated) }

                    binding.textMentorExpertise.text = profile.mentorExpertise.ifBlank { getString(R.string.placeholder_not_updated) }
                    binding.textMentorExperience.text = profile.mentorExperience.ifBlank { getString(R.string.placeholder_not_updated) }
                    binding.textMentorRate.text = profile.mentorRate.ifBlank { getString(R.string.placeholder_not_updated) }
                    
                    // Generate default avatar based on first letter of profile name
                    val avatar = AvatarGenerator.generateAvatar(profile.name, requireContext())
                    binding.imageProfile.setImageDrawable(avatar)
                }
                is Resource.Error -> {
                    setLoading(false)
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                Resource.Idle -> setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressIndicator.isVisible = isLoading
        binding.cardProfileDetails.isVisible = !isLoading
        binding.imageProfile.isVisible = !isLoading
        binding.textProfileName.isVisible = !isLoading
        binding.textProfileEmail.isVisible = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
