package com.example.mentorconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.UserProfile
import com.example.mentorconnect.data.repository.AuthRepository
import com.example.mentorconnect.data.repository.UserRepository
import com.example.mentorconnect.data.util.Resource
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _profileState = MutableLiveData<Resource<UserProfile>>(Resource.Idle)
    val profileState: LiveData<Resource<UserProfile>> = _profileState

    fun refreshProfile() {
        val user = authRepository.currentUser()
        if (user == null) {
            _profileState.value = Resource.Error("You are not signed in")
            return
        }
        _profileState.value = Resource.Loading
        viewModelScope.launch {
            val result = userRepository.getUserProfile(user.uid)
            _profileState.postValue(result)
        }
    }

    class Factory(
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ProfileViewModel::class.java) { "Unknown ViewModel class" }
            return ProfileViewModel(userRepository, authRepository) as T
        }
    }
}
