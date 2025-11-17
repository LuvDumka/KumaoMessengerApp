package com.example.mentorconnect.ui.helpbot

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentorconnect.R
import com.example.mentorconnect.databinding.ActivityHelpMentorSelectionBinding
import com.example.mentorconnect.ui.helpbot.adapter.HelpMentorChatAdapter
import com.example.mentorconnect.ui.main.MainActivity

class HelpMentorSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpMentorSelectionBinding
    private val viewModel: HelpMentorSelectionViewModel by viewModels()
    private val chatAdapter = HelpMentorChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpMentorSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupInput()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.buttonSkip.setOnClickListener { navigateToMain() }
        binding.textQuickSuggestions.setOnClickListener { viewModel.skipToRecommendations() }
    }

    private fun setupRecyclerView() {
        binding.recyclerMessages.apply {
            layoutManager = LinearLayoutManager(this@HelpMentorSelectionActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupInput() {
        binding.textInputMessage.editText?.doAfterTextChanged {
            binding.buttonSend.isEnabled = !it.isNullOrBlank()
        }
        binding.buttonSend.setOnClickListener {
            val text = binding.textInputMessage.editText?.text?.toString().orEmpty()
            if (text.isBlank()) {
                binding.textInputMessage.error = getString(R.string.help_mentor_selection_input_error)
            } else {
                binding.textInputMessage.error = null
                viewModel.sendUserMessage(text)
                binding.textInputMessage.editText?.setText("")
            }
        }
        binding.buttonFinish.setOnClickListener {
            navigateToMain()
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages) {
                binding.recyclerMessages.scrollToPosition(maxOf(messages.size - 1, 0))
            }
        }
        viewModel.canFinish.observe(this) { canFinish ->
            binding.buttonFinish.visibility = if (canFinish) View.VISIBLE else View.GONE
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
