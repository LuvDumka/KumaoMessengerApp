package com.example.mentorconnect.ui.mentor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentorconnect.R
import com.example.mentorconnect.data.model.VideoCallSession
import com.example.mentorconnect.databinding.FragmentMentorVideoCallsBinding
import com.example.mentorconnect.ui.videocall.IncomingCallsViewModel
import com.example.mentorconnect.ui.videocall.VideoCallActivity
import com.google.firebase.auth.FirebaseAuth

class MentorVideoCallsFragment : Fragment() {

    private var _binding: FragmentMentorVideoCallsBinding? = null
    private val binding get() = _binding!!

    private val incomingCallsViewModel: IncomingCallsViewModel by viewModels()
    private lateinit var incomingCallsAdapter: IncomingCallAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMentorVideoCallsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupIncomingCallsList()
        observeIncomingCalls()
    }

    private fun setupIncomingCallsList() {
        incomingCallsAdapter = IncomingCallAdapter(::joinIncomingCall)
        binding.recyclerViewIncomingCalls.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = incomingCallsAdapter
        }
    }

    private fun observeIncomingCalls() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            showEmptyState(getString(R.string.error_auth_required))
            return
        }

        setLoading(true)
        incomingCallsViewModel.startListening(currentUserId)
        incomingCallsViewModel.incomingCalls.observe(viewLifecycleOwner) { calls ->
            setLoading(false)
            if (calls.isNullOrEmpty()) {
                binding.textIncomingCallsTitle.visibility = View.GONE
                binding.recyclerViewIncomingCalls.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
                binding.textEmptyState.text = getString(R.string.mentor_incoming_calls_empty_state)
            } else {
                binding.textIncomingCallsTitle.visibility = View.VISIBLE
                binding.recyclerViewIncomingCalls.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
                incomingCallsAdapter.submitList(calls)
            }
        }
    }

    private fun joinIncomingCall(session: VideoCallSession) {
        val intent = Intent(requireContext(), VideoCallActivity::class.java).apply {
            putExtra(VideoCallActivity.EXTRA_OTHER_USER_ID, session.hostUserId)
            putExtra(VideoCallActivity.EXTRA_OTHER_USER_NAME, getString(R.string.student_placeholder))
            putExtra("DIRECT_CALL", true)
            putExtra("EXISTING_SESSION_ID", session.id)
            putExtra("CHANNEL_NAME", session.channelName)
        }
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(message: String = getString(R.string.mentor_incoming_calls_empty_state)) {
        binding.textIncomingCallsTitle.visibility = View.GONE
        binding.recyclerViewIncomingCalls.visibility = View.GONE
        binding.textEmptyState.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
