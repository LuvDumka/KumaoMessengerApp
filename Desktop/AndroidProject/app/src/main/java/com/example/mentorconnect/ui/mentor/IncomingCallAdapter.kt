package com.example.mentorconnect.ui.mentor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentorconnect.R
import com.example.mentorconnect.data.model.VideoCallSession
import com.example.mentorconnect.databinding.ItemIncomingCallBinding
import java.text.SimpleDateFormat
import java.util.*

class IncomingCallAdapter(
    private val onJoinClick: (VideoCallSession) -> Unit
) : ListAdapter<VideoCallSession, IncomingCallAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncomingCallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemIncomingCallBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(session: VideoCallSession) {
            binding.tvCallerName.text = "Incoming Call"
            binding.tvCallerId.text = "From: ${session.hostUserId}"
            
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            binding.tvCallTime.text = "Started: ${timeFormat.format(Date(session.startTime))}"
            
            binding.btnJoinCall.setOnClickListener {
                onJoinClick(session)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VideoCallSession>() {
        override fun areItemsTheSame(oldItem: VideoCallSession, newItem: VideoCallSession): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoCallSession, newItem: VideoCallSession): Boolean {
            return oldItem == newItem
        }
    }
}
