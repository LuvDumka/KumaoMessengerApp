package com.example.mentorconnect.ui.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.TimeSlot
import com.example.mentorconnect.data.repository.BookingRepository
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class BookingViewModel(
    private val bookingRepository: BookingRepository = BookingRepository()
) : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    
    private val _slots = MutableLiveData<Resource<List<TimeSlot>>>()
    val slots: LiveData<Resource<List<TimeSlot>>> = _slots
    
    private val _bookingResult = MutableLiveData<Resource<Boolean>>()
    val bookingResult: LiveData<Resource<Boolean>> = _bookingResult
    
    private val _myBookings = MutableLiveData<Resource<List<TimeSlot>>>()
    val myBookings: LiveData<Resource<List<TimeSlot>>> = _myBookings
    
    fun loadSlotsForDate(mentorId: String, date: String) {
        _slots.value = Resource.Loading
        viewModelScope.launch {
            android.util.Log.d("BookingViewModel", "Loading slots for mentor: $mentorId, date: $date")
            val result = bookingRepository.getAllSlotsForDate(mentorId, date)
            android.util.Log.d("BookingViewModel", "Result: success=${result.isSuccess}, data=${result.getOrNull()?.size ?: 0} slots")
            _slots.value = if (result.isSuccess) {
                val slots = result.getOrNull() ?: emptyList()
                if (slots.isEmpty()) {
                    android.util.Log.w("BookingViewModel", "No slots found for $mentorId on $date")
                }
                Resource.Success(slots)
            } else {
                android.util.Log.e("BookingViewModel", "Failed to load slots: ${result.exceptionOrNull()?.message}")
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to load slots")
            }
        }
    }
    
    fun bookSlot(slotId: String, studentMessage: String) {
        _bookingResult.value = Resource.Loading
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _bookingResult.value = Resource.Error("User not authenticated")
                return@launch
            }
            
            val userName = user.displayName ?: user.email ?: "Unknown User"
            val result = bookingRepository.bookSlot(slotId, user.uid, userName, studentMessage)
            
            _bookingResult.value = if (result.isSuccess) {
                Resource.Success(true)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to book slot")
            }
        }
    }
    
    fun cancelBooking(slotId: String) {
        _bookingResult.value = Resource.Loading
        viewModelScope.launch {
            val result = bookingRepository.cancelBooking(slotId)
            _bookingResult.value = if (result.isSuccess) {
                Resource.Success(true)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to cancel booking")
            }
        }
    }
    
    fun loadMyBookings() {
        _myBookings.value = Resource.Loading
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _myBookings.value = Resource.Error("User not authenticated")
                return@launch
            }
            
            val result = bookingRepository.getMyBookings(user.uid)
            _myBookings.value = if (result.isSuccess) {
                Resource.Success(result.getOrNull() ?: emptyList())
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to load bookings")
            }
        }
    }
}
