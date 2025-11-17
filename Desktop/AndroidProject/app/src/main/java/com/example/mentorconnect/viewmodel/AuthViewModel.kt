package com.example.mentorconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.repository.AuthRepository
import com.example.mentorconnect.data.util.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<Resource<Unit>>(Resource.Idle)
    val authState: LiveData<Resource<Unit>> = _authState
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
        _authState.value = Resource.Error("Please provide email and password")
            return
        }
        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.login(email.trim(), password)
            _authState.postValue(result)
        }
    }
    fun signup(
        name: String,
        email: String,
        password: String,
        studentEducation: String,
        studentInterest: String,
        mentorExpertise: String,
        mentorExperience: String,
        mentorRate: String,
        role: String = "STUDENT"
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
        _authState.value = Resource.Error("All fields are required")
            return
        }

        if (role == "STUDENT") {
            if (studentEducation.isBlank() || studentInterest.isBlank()) {
                _authState.value = Resource.Error("Please complete your education and interest details")
                return
            }
        } else {
            if (mentorExpertise.isBlank() || mentorExperience.isBlank() || mentorRate.isBlank()) {
                _authState.value = Resource.Error("Please share your expertise, experience, and pricing")
                return
            }
        }
        _authState.value = Resource.Loading
        viewModelScope.launch {
            val result = repository.signup(
                name.trim(),
                email.trim(),
                password,
                studentEducation.trim(),
                studentInterest.trim(),
                mentorExpertise.trim(),
                mentorExperience.trim(),
                mentorRate.trim(),
                role
            )
            _authState.postValue(result)
        }
    }

    fun logout() {
        repository.logout()
    }

    fun resetState() {
    _authState.value = Resource.Idle
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == AuthViewModel::class.java) {
                "Unknown ViewModel class"
            }
            return AuthViewModel(repository) as T
        }
    }
}
