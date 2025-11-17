package com.example.mentorconnect.ui.main.mentor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.databinding.FragmentMentorListBinding
import com.example.mentorconnect.viewmodel.MentorViewModel

class MentorListFragment : Fragment() {

    private var _binding: FragmentMentorListBinding? = null
    private val binding get() = _binding!!

    private val mentorAdapter = MentorAdapter()
    private val viewModel: MentorViewModel by viewModels {
        MentorViewModel.Factory(ServiceLocator.provideMentorRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMentorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMentors.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMentors.adapter = mentorAdapter

        viewModel.mentors.observe(viewLifecycleOwner) { mentors ->
            mentorAdapter.submitList(mentors)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
