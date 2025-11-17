package com.example.mentorconnect.ui.videocall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.CallStatus
import com.example.mentorconnect.data.model.VideoCallSession
import com.example.mentorconnect.data.repository.VideoCallRepository
import com.example.mentorconnect.data.util.Resource
import kotlinx.coroutines.launch
import java.util.*

class VideoCallViewModel(
    private val repository: VideoCallRepository = VideoCallRepository()
) : ViewModel() {
    
    private val _callSession = MutableLiveData<Resource<VideoCallSession>>()
    val callSession: LiveData<Resource<VideoCallSession>> = _callSession
    
    private val _callEnd = MutableLiveData<Resource<Boolean>>()
    val callEnd: LiveData<Resource<Boolean>> = _callEnd
    
    fun initializeCallSession(
        slotId: String,
        hostUserId: String,
        guestUserId: String,
        startTime: Long,
        endTime: Long
    ) {
        _callSession.value = Resource.Loading
        viewModelScope.launch {
            try {
                // For direct calls (slotId starts with "direct_"), skip checking for existing session
                val isDirectCall = slotId.startsWith("direct_")
                
                if (!isDirectCall) {
                    // Only check for existing session if it's a slot-based call
                    val existingSessionResult = repository.getCallSession(slotId)
                    val existingSession = existingSessionResult.getOrNull()

                    if (existingSession != null) {
                        _callSession.value = Resource.Success(existingSession)
                        return@launch
                    }
                }

                // Create new session
                val channelName = "mentor_call_${UUID.randomUUID().toString().take(8)}"
                val session = VideoCallSession(
                    slotId = slotId,
                    hostUserId = hostUserId,
                    guestUserId = guestUserId,
                    channelName = channelName,
                    startTime = startTime,
                    endTime = endTime,
                    status = CallStatus.ACTIVE
                )

                val result = repository.createCallSession(session)
                _callSession.value = if (result.isSuccess) {
                    Resource.Success(session.copy(id = result.getOrNull() ?: ""))
                } else {
                    Resource.Error(result.exceptionOrNull()?.message ?: "Failed to create call session")
                }
            } catch (e: Exception) {
                _callSession.value = Resource.Error("Error initializing call: ${e.message}")
            }
        }
    }
    
    fun endCall(sessionId: String) {
        _callEnd.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.endCallSession(sessionId)
            _callEnd.value = if (result.isSuccess) {
                Resource.Success(true)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to end call")
            }
        }
    }
}
