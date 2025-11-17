package com.example.mentorconnect.ui.mentor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentorconnect.data.model.ChatThread
import com.example.mentorconnect.databinding.ItemMentorChatBinding
import com.example.mentorconnect.util.AvatarGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MentorChatListAdapter(
    private val onThreadClicked: (ChatThread) -> Unit
) : ListAdapter<ChatThread, MentorChatListAdapter.ChatThreadViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatThreadViewHolder {
        val binding = ItemMentorChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatThreadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatThreadViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatThreadViewHolder(
        private val binding: ItemMentorChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(thread: ChatThread) {
            binding.textName.text = thread.otherUserName
            binding.textLastMessage.text = thread.lastMessage.ifBlank { "Tap to start chatting" }
            binding.textTimestamp.text = formatTime(thread.lastMessageTimestamp)

            val avatar = AvatarGenerator.generateAvatar(thread.otherUserName, binding.root.context)
            binding.imageAvatar.setImageDrawable(avatar)

            if (thread.unreadCount > 0) {
                binding.chipUnread.visibility = View.VISIBLE
                binding.chipUnread.text = thread.unreadCount.toString()
            } else {
                binding.chipUnread.visibility = View.GONE
            }

            binding.root.setOnClickListener { onThreadClicked(thread) }
        }

        private fun formatTime(timestamp: Long): String {
            if (timestamp == 0L) return "--"
            val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ChatThread>() {
        override fun areItemsTheSame(oldItem: ChatThread, newItem: ChatThread): Boolean =
            oldItem.conversationId == newItem.conversationId

        override fun areContentsTheSame(oldItem: ChatThread, newItem: ChatThread): Boolean =
            oldItem == newItem
    }
}
