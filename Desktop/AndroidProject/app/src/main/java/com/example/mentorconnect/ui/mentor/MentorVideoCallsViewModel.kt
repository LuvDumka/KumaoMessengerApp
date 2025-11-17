package com.example.mentorconnect.ui.mentor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.TimeSlot
import com.example.mentorconnect.data.repository.BookingRepository
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MentorVideoCallsViewModel(
    private val bookingRepository: BookingRepository = BookingRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _sessions = MutableLiveData<Resource<List<TimeSlot>>>(Resource.Idle)
    val sessions: LiveData<Resource<List<TimeSlot>>> = _sessions

    fun refreshSessions() {
        val mentorId = auth.currentUser?.uid ?: run {
            _sessions.value = Resource.Error("User not authenticated")
            return
        }

        _sessions.value = Resource.Loading
        viewModelScope.launch {
            val result = bookingRepository.getMentorSessions(mentorId)
            _sessions.value = if (result.isSuccess) {
                Resource.Success(result.getOrDefault(emptyList()))
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to load sessions")
            }
        }
    }
}
