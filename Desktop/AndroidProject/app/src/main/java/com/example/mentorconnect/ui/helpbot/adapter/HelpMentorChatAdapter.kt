package com.example.mentorconnect.ui.helpbot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentorconnect.databinding.ItemHelpBotMessageBinding
import com.example.mentorconnect.databinding.ItemHelpUserMessageBinding
import com.example.mentorconnect.ui.helpbot.model.HelpBotMessage

class HelpMentorChatAdapter : ListAdapter<HelpBotMessage, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_BOT = 0
        private const val VIEW_TYPE_USER = 1

        private val DiffCallback = object : DiffUtil.ItemCallback<HelpBotMessage>() {
            override fun areItemsTheSame(oldItem: HelpBotMessage, newItem: HelpBotMessage): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: HelpBotMessage, newItem: HelpBotMessage): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).isBot) VIEW_TYPE_BOT else VIEW_TYPE_USER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_BOT) {
            val binding = ItemHelpBotMessageBinding.inflate(inflater, parent, false)
            BotMessageViewHolder(binding)
        } else {
            val binding = ItemHelpUserMessageBinding.inflate(inflater, parent, false)
            UserMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is BotMessageViewHolder -> holder.bind(message)
            is UserMessageViewHolder -> holder.bind(message)
        }
    }

    class BotMessageViewHolder(private val binding: ItemHelpBotMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: HelpBotMessage) {
            binding.tvMessage.text = message.text
        }
    }

    class UserMessageViewHolder(private val binding: ItemHelpUserMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: HelpBotMessage) {
            binding.tvMessage.text = message.text
        }
    }
}
