package com.example.mentorconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.Mentor
import com.example.mentorconnect.data.repository.MentorRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MentorViewModel(private val repository: MentorRepository) : ViewModel() {

    private val _mentors = MutableLiveData<List<Mentor>>()
    val mentors: LiveData<List<Mentor>> = _mentors

    init {
        viewModelScope.launch {
            repository.listenToMentors().collectLatest { mentorList ->
                _mentors.postValue(mentorList)
            }
        }
    }

    class Factory(private val repository: MentorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == MentorViewModel::class.java) { "Unknown ViewModel class" }
            return MentorViewModel(repository) as T
        }
    }
}
