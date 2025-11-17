package com.example.mentorconnect.ui.mentor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.ChatThread
import com.example.mentorconnect.data.repository.ChatRepository
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MentorChatsViewModel(
    private val chatRepository: ChatRepository = ChatRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _threadState = MutableLiveData<Resource<List<ChatThread>>>()
    val threadState: LiveData<Resource<List<ChatThread>>> = _threadState

    private var listenJob: Job? = null

    fun startListening() {
        val mentorId = auth.currentUser?.uid ?: run {
            _threadState.value = Resource.Error("User not authenticated")
            return
        }

        _threadState.value = Resource.Loading
        listenJob?.cancel()
        listenJob = viewModelScope.launch {
            try {
                chatRepository.observeThreads(mentorId)
                    .catch { throwable ->
                        _threadState.postValue(Resource.Error(throwable.message ?: "Failed to load chats"))
                    }
                    .collect { threads ->
                        _threadState.postValue(Resource.Success(threads))
                    }
            } catch (e: Exception) {
                _threadState.postValue(Resource.Error(e.message ?: "Failed to load chats"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenJob?.cancel()
    }
}
