package com.example.mentorconnect.ui.mentor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentorconnect.data.model.ChatThread
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.databinding.FragmentMentorChatsBinding
import com.example.mentorconnect.ui.chat.ChatActivity

class MentorChatsFragment : Fragment() {

    private var _binding: FragmentMentorChatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MentorChatsViewModel by viewModels()
    private lateinit var chatsAdapter: MentorChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMentorChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeThreads()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startListening()
    }

    private fun setupRecyclerView() {
        chatsAdapter = MentorChatListAdapter(::openChat)
        binding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatsAdapter
        }
    }

    private fun observeThreads() {
        viewModel.threadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> {
                    setLoading(false)
                    val data = state.data.orEmpty()
                    if (data.isEmpty()) {
                        showEmptyState()
                    } else {
                        binding.textEmptyState.visibility = View.GONE
                        binding.recyclerViewChats.visibility = View.VISIBLE
                        chatsAdapter.submitList(data)
                    }
                }
                is Resource.Error -> {
                    setLoading(false)
                    showEmptyState(state.message ?: "No chats yet")
                }
                Resource.Idle -> Unit
            }
        }
    }

    private fun openChat(thread: ChatThread) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_USER_ID, thread.otherUserId)
            putExtra(ChatActivity.EXTRA_USER_NAME, thread.otherUserName)
        }
        startActivity(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        if (isLoading) {
            binding.recyclerViewChats.visibility = View.GONE
            binding.textEmptyState.visibility = View.GONE
        }
    }

    private fun showEmptyState(message: String = getString(com.example.mentorconnect.R.string.mentor_chat_empty_state)) {
        binding.textEmptyState.text = message
        binding.textEmptyState.visibility = View.VISIBLE
        binding.recyclerViewChats.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
