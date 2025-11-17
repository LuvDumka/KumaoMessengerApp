package com.example.mentorconnect.ui.booking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mentorconnect.R
import com.example.mentorconnect.data.model.Mentor
import com.example.mentorconnect.databinding.ActivityMentorDetailBinding
import com.example.mentorconnect.ui.chat.ChatActivity
import com.example.mentorconnect.util.AvatarGenerator
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MentorDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMentorDetailBinding
    private lateinit var mentor: Mentor
    
    companion object {
        const val EXTRA_MENTOR = "extra_mentor"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMentorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        mentor = intent.getParcelableExtra<Mentor>(EXTRA_MENTOR) ?: run {
            finish()
            return
        }
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        binding.tvMentorName.text = mentor.name
        binding.tvMentorExpertise.text = mentor.expertise
        binding.tvMentorBio.text = mentor.bio
        binding.tvMentorExperience.text = mentor.experience
        binding.tvMentorRating.text = getString(R.string.rating) + " " + mentor.rating.toString() + " ⭐"
        binding.tvSessionPrice.text = getString(R.string.price_per_hour_value, mentor.hourlyRate)
        
        // Generate default avatar based on first letter of mentor name
        val avatar = AvatarGenerator.generateAvatar(mentor.name, this)
        if (!mentor.avatarUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(mentor.avatarUrl)
                .placeholder(avatar)
                .error(avatar)
                .into(binding.ivMentorImage)
        } else {
            binding.ivMentorImage.setImageDrawable(avatar)
        }
    }
    
    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        binding.ivChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_USER_ID, mentor.id)
            intent.putExtra(ChatActivity.EXTRA_USER_NAME, mentor.name)
            startActivity(intent)
        }
        
        binding.btnVideoCall.setOnClickListener {
            startDirectVideoCall()
        }
    }
    
    private fun startDirectVideoCall() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "Please login to start a call", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Create a direct call session without slot requirements
        val intent = Intent(this, com.example.mentorconnect.ui.videocall.VideoCallActivity::class.java)
        intent.putExtra(com.example.mentorconnect.ui.videocall.VideoCallActivity.EXTRA_OTHER_USER_ID, mentor.id)
        intent.putExtra(com.example.mentorconnect.ui.videocall.VideoCallActivity.EXTRA_OTHER_USER_NAME, mentor.name)
        intent.putExtra("DIRECT_CALL", true) // Flag to indicate direct call without slot
        startActivity(intent)
    }
}
