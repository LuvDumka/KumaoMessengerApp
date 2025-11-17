package com.example.mentorconnect.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentorconnect.R
import com.example.mentorconnect.databinding.ActivityChatBinding
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.ui.videocall.VideoCallActivity
import com.example.mentorconnect.util.AvatarGenerator
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private var otherUserId: String = ""
    private var otherUserName: String = ""
    
    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_USER_NAME = "extra_user_name"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        otherUserId = intent.getStringExtra(EXTRA_USER_ID) ?: run {
            finish()
            return
        }
        otherUserName = intent.getStringExtra(EXTRA_USER_NAME) ?: "User"
        
    viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

    setupUI()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

    viewModel.loadMessages(otherUserId, otherUserName)
    }
    
    private fun setupUI() {
        binding.tvUserName.text = otherUserName
        
        // Generate default avatar based on first letter of user name
        val avatar = AvatarGenerator.generateAvatar(otherUserName, this)
        binding.ivUserImage.setImageDrawable(avatar)
    }
    
    private fun setupRecyclerView() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        chatAdapter = ChatAdapter(currentUserId)
        
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvMessages.smoothScrollToPosition(messages.size - 1)
            }
        }
        
        viewModel.sendResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.ivSend.isEnabled = false
                }
                is Resource.Success -> {
                    binding.ivSend.isEnabled = true
                    binding.etMessage.text?.clear()
                }
                is Resource.Error -> {
                    binding.ivSend.isEnabled = true
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        binding.ivSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(otherUserId, otherUserName, message)
            }
        }
        
        binding.ivVideoCall.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Please sign in to start a video call", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, VideoCallActivity::class.java).apply {
                putExtra(VideoCallActivity.EXTRA_OTHER_USER_ID, otherUserId)
                putExtra(VideoCallActivity.EXTRA_OTHER_USER_NAME, otherUserName)
                putExtra("DIRECT_CALL", true)
            }
            startActivity(intent)
        }
    }
}
