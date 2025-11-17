package com.example.mentorconnect.ui.videocall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mentorconnect.data.model.VideoCallSession
import com.example.mentorconnect.data.repository.VideoCallRepository

class IncomingCallsViewModel(
    private val repository: VideoCallRepository = VideoCallRepository()
) : ViewModel() {
    
    private val _incomingCalls = MutableLiveData<List<VideoCallSession>>()
    val incomingCalls: LiveData<List<VideoCallSession>> = _incomingCalls
    
    fun startListening(userId: String) {
        repository.observeIncomingCalls(userId) { sessions ->
            _incomingCalls.postValue(sessions)
        }
    }
}
