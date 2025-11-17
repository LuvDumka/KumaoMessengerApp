package com.example.mentorconnect.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentorconnect.data.model.ChatMessage
import com.example.mentorconnect.data.model.MessageType
import com.example.mentorconnect.data.repository.ChatRepository
import com.example.mentorconnect.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class ChatViewModel(
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    private var activePartnerId: String? = null
    private var activePartnerName: String? = null
    private var messagesJob: Job? = null
    
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages
    
    private val _sendResult = MutableLiveData<Resource<Boolean>>()
    val sendResult: LiveData<Resource<Boolean>> = _sendResult
    
    fun loadMessages(otherUserId: String, otherUserName: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        activePartnerId = otherUserId
        activePartnerName = otherUserName
        messagesJob?.cancel()

        messagesJob = viewModelScope.launch {
            chatRepository.getMessages(currentUserId, otherUserId).collect { messageList ->
                _messages.value = messageList
            }
        }
    }
    
    fun sendMessage(receiverId: String, receiverName: String, messageText: String) {
        _sendResult.value = Resource.Loading
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _sendResult.value = Resource.Error("User not authenticated")
                return@launch
            }
            
            val message = ChatMessage(
                senderId = user.uid,
                senderName = user.displayName ?: user.email ?: "Unknown",
                receiverId = receiverId,
                message = messageText,
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT
            )
            
            val result = chatRepository.sendMessage(message, receiverName)
            _sendResult.value = if (result.isSuccess) {
                Resource.Success(true)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Failed to send message")
            }
        }
    }
}
