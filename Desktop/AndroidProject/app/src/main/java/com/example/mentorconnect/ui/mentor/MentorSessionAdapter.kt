package com.example.mentorconnect.ui.mentor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentorconnect.R
import com.example.mentorconnect.data.model.SlotStatus
import com.example.mentorconnect.data.model.TimeSlot
import com.example.mentorconnect.databinding.ItemMentorSessionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MentorSessionAdapter(
    private val onStartCallClicked: (TimeSlot) -> Unit
) : ListAdapter<TimeSlot, MentorSessionAdapter.SessionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemMentorSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SessionViewHolder(
        private val binding: ItemMentorSessionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(slot: TimeSlot) {
            binding.textStudentName.text = slot.bookedByUserName.ifBlank { "Student" }
            binding.textDate.text = slot.date
            binding.textTime.text = "${slot.startTime} - ${slot.endTime}"
            binding.chipStatus.text = slot.status.name
            binding.textStudentNotes.text = slot.studentMessage.ifBlank {
                binding.root.context.getString(R.string.student_message_empty_fallback)
            }

            val isJoinable = slot.status == SlotStatus.BOOKED && isSlotCurrent(slot)
            binding.buttonStartCall.isEnabled = isJoinable
            binding.buttonStartCall.alpha = if (isJoinable) 1f else 0.5f

            binding.buttonStartCall.setOnClickListener {
                onStartCallClicked(slot)
            }
        }

        private fun isSlotCurrent(slot: TimeSlot): Boolean {
            return try {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val start = formatter.parse("${slot.date} ${slot.startTime}")?.time ?: 0L
                val end = formatter.parse("${slot.date} ${slot.endTime}")?.time ?: 0L
                val now = Date().time
                now in start..end
            } catch (e: Exception) {
                false
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
        override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean =
            oldItem == newItem
    }
}
