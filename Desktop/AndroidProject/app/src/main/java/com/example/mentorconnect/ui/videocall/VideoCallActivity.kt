package com.example.mentorconnect.ui.videocall

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mentorconnect.BuildConfig
import com.example.mentorconnect.R
import com.example.mentorconnect.data.model.TimeSlot
import com.example.mentorconnect.data.util.Resource
import com.example.mentorconnect.databinding.ActivityVideoCallBinding
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import java.text.SimpleDateFormat
import java.util.*

class VideoCallActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoCallBinding
    private lateinit var viewModel: VideoCallViewModel
    private var rtcEngine: RtcEngine? = null
    private var timeSlot: TimeSlot? = null
    private var callSessionId: String? = null
    private var callEndTime: Long = 0L // Store end time for direct calls
    
    private var isMuted = false
    private var isVideoEnabled = true
    private var callStartTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private var durationRunnable: Runnable? = null
    
    companion object {
        const val EXTRA_TIME_SLOT = "extra_time_slot"
        const val EXTRA_OTHER_USER_ID = "extra_other_user_id"
        const val EXTRA_OTHER_USER_NAME = "extra_other_user_name"
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private val agoraAppId: String
        get() = BuildConfig.AGORA_APP_ID
    
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        timeSlot = intent.getParcelableExtra(EXTRA_TIME_SLOT)
        val otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: ""
        val otherUserName = intent.getStringExtra(EXTRA_OTHER_USER_NAME) ?: ""
        val isDirectCall = intent.getBooleanExtra("DIRECT_CALL", false)
        val existingSessionId = intent.getStringExtra("EXISTING_SESSION_ID")
        val existingChannelName = intent.getStringExtra("CHANNEL_NAME")
        
        // For direct calls, timeSlot is optional
        if (timeSlot == null && !isDirectCall) {
            Toast.makeText(this, "Invalid call session", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        viewModel = ViewModelProvider(this)[VideoCallViewModel::class.java]
        
        if (checkPermissions()) {
            if (existingSessionId != null && existingChannelName != null) {
                // Joining an existing call (mentor side)
                joinExistingCall(existingSessionId, existingChannelName, otherUserId, otherUserName)
            } else {
                // Creating a new call (student side)
                initializeCall(otherUserId, otherUserName, isDirectCall)
            }
        } else {
            requestPermissions()
        }
        
        setupClickListeners()
        setupObservers()
    }
    
    private fun checkPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: ""
                val otherUserName = intent.getStringExtra(EXTRA_OTHER_USER_NAME) ?: ""
                val isDirectCall = intent.getBooleanExtra("DIRECT_CALL", false)
                initializeCall(otherUserId, otherUserName, isDirectCall)
            } else {
                Toast.makeText(this, "Permissions required for video call", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun initializeCall(otherUserId: String, otherUserName: String, isDirectCall: Boolean = false) {
        binding.tvCallStatus.text = otherUserName
        
        try {
            if (agoraAppId.isBlank()) {
                Toast.makeText(this, "Missing Agora App ID", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            val config = RtcEngineConfig()
            config.mContext = applicationContext
            config.mAppId = agoraAppId
            config.mEventHandler = rtcEventHandler
            
            rtcEngine = RtcEngine.create(config)
            rtcEngine?.enableVideo()
            
            // Setup local video
            val localVideoCanvas = VideoCanvas(binding.localVideoContainer, VideoCanvas.RENDER_MODE_FIT, 0)
            rtcEngine?.setupLocalVideo(localVideoCanvas)
            rtcEngine?.startPreview()
            
            // Create call session
            val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            
            if (isDirectCall) {
                // Direct call without slot - use current time and 1 hour duration
                val currentTime = System.currentTimeMillis()
                callEndTime = currentTime + (60 * 60 * 1000) // 1 hour from now
                
                viewModel.initializeCallSession(
                    "direct_${currentUserId}_${otherUserId}_${currentTime}",
                    currentUserId,
                    otherUserId,
                    currentTime,
                    callEndTime
                )
            } else {
                // Slot-based call (legacy support)
                val slotTime = parseSlotTime(timeSlot!!.date, timeSlot!!.startTime)
                callEndTime = parseSlotTime(timeSlot!!.date, timeSlot!!.endTime)
                
                viewModel.initializeCallSession(
                    timeSlot!!.id,
                    currentUserId,
                    otherUserId,
                    slotTime,
                    callEndTime
                )
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to initialize call: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupObservers() {
        viewModel.callSession.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.connectingLayout.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.connectingLayout.visibility = View.GONE
                    callSessionId = resource.data.id
                    
                    // Store end time from session if not already set
                    if (callEndTime == 0L) {
                        callEndTime = resource.data.endTime
                    }
                    
                    joinChannel(resource.data.channelName)
                    startCallDuration()
                    showTimeRemaining()
                }
                is Resource.Error -> {
                    binding.connectingLayout.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
        
        viewModel.callEnd.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {}
            }
        }
    }
    
    private fun joinChannel(channelName: String) {
        val options = ChannelMediaOptions()
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        
        // Join with null token for testing (use token server in production)
        rtcEngine?.joinChannel(null, channelName, 0, options)
    }
    
    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
                binding.tvCallStatus.text = "Connected"
            }
        }
        
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                binding.tvCallStatus.text = "User disconnected"
                Toast.makeText(this@VideoCallActivity, "Other user left the call", Toast.LENGTH_SHORT).show()
            }
        }
        
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                binding.tvCallStatus.text = "Waiting for other user..."
            }
        }
    }
    
    private fun setupRemoteVideo(uid: Int) {
        val remoteVideoCanvas = VideoCanvas(binding.remoteVideoContainer, VideoCanvas.RENDER_MODE_FIT, uid)
        rtcEngine?.setupRemoteVideo(remoteVideoCanvas)
    }
    
    private fun setupClickListeners() {
        binding.btnEndCall.setOnClickListener {
            endCall()
        }
        
        binding.btnMuteAudio.setOnClickListener {
            isMuted = !isMuted
            rtcEngine?.muteLocalAudioStream(isMuted)
            binding.btnMuteAudio.setImageResource(
                if (isMuted) R.drawable.ic_mic_off else R.drawable.ic_mic
            )
        }
        
        binding.btnToggleVideo.setOnClickListener {
            isVideoEnabled = !isVideoEnabled
            rtcEngine?.muteLocalVideoStream(!isVideoEnabled)
            binding.btnToggleVideo.setImageResource(
                if (isVideoEnabled) R.drawable.ic_videocam else R.drawable.ic_videocam_off
            )
            binding.localVideoContainer.visibility = if (isVideoEnabled) View.VISIBLE else View.GONE
        }
        
        binding.btnSwitchCamera.setOnClickListener {
            rtcEngine?.switchCamera()
        }
    }
    
    private fun startCallDuration() {
        callStartTime = System.currentTimeMillis()
        durationRunnable = object : Runnable {
            override fun run() {
                val duration = (System.currentTimeMillis() - callStartTime) / 1000
                val minutes = duration / 60
                val seconds = duration % 60
                binding.tvCallDuration.text = String.format("%02d:%02d", minutes, seconds)
                
                // Check if call time has ended
                if (isCallTimeEnded()) {
                    endCall()
                    Toast.makeText(
                        this@VideoCallActivity,
                        "Call time has ended",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(durationRunnable!!)
    }
    
    private fun showTimeRemaining() {
        if (callEndTime == 0L) return // Skip if end time not set
        
        val remainingTime = (callEndTime - System.currentTimeMillis()) / 1000 / 60
        
        if (remainingTime > 0) {
            binding.tvTimeRemaining.visibility = View.VISIBLE
            binding.tvTimeRemaining.text = "Time remaining: $remainingTime min"
        }
    }
    
    private fun isCallTimeEnded(): Boolean {
        if (callEndTime == 0L) return false // Don't end if time not set
        return System.currentTimeMillis() > callEndTime
    }
    
    private fun joinExistingCall(sessionId: String, channelName: String, otherUserId: String, otherUserName: String) {
        binding.tvCallStatus.text = otherUserName
        
        try {
            if (agoraAppId.isBlank()) {
                Toast.makeText(this, "Missing Agora App ID", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            val config = RtcEngineConfig()
            config.mContext = applicationContext
            config.mAppId = agoraAppId
            config.mEventHandler = rtcEventHandler
            
            rtcEngine = RtcEngine.create(config)
            rtcEngine?.enableVideo()
            
            // Setup local video
            val localVideoCanvas = VideoCanvas(binding.localVideoContainer, VideoCanvas.RENDER_MODE_FIT, 0)
            rtcEngine?.setupLocalVideo(localVideoCanvas)
            rtcEngine?.startPreview()
            
            // Store session info
            callSessionId = sessionId
            callEndTime = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour default
            
            // Join the existing channel directly
            joinChannel(channelName)
            startCallDuration()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to join call: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun parseSlotTime(date: String, time: String): Long {
        return try {
            val dateTimeStr = "$date $time"
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            format.parse(dateTimeStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    private fun endCall() {
        durationRunnable?.let { handler.removeCallbacks(it) }
        callSessionId?.let { viewModel.endCall(it) }
        rtcEngine?.leaveChannel()
        rtcEngine?.stopPreview()
        RtcEngine.destroy()
        rtcEngine = null
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        endCall()
    }
}
