package com.example.mentorconnect.ui.booking

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentorconnect.R
import com.example.mentorconnect.data.model.SlotStatus
import com.example.mentorconnect.data.model.TimeSlot
import com.example.mentorconnect.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(
    private val onSlotClick: (TimeSlot) -> Unit
) : ListAdapter<TimeSlot, TimeSlotAdapter.SlotViewHolder>(SlotDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SlotViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class SlotViewHolder(
        private val binding: ItemTimeSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(slot: TimeSlot) {
            binding.tvSlotTime.text = slot.startTime
            
            when (slot.status) {
                SlotStatus.AVAILABLE -> {
                    binding.tvSlotStatus.text = binding.root.context.getString(R.string.status_available)
                    binding.slotContainer.setBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.green_light)
                    )
                    binding.tvSlotStatus.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.green_dark)
                    )
                    binding.root.isEnabled = true
                }
                SlotStatus.BOOKED -> {
                    binding.tvSlotStatus.text = binding.root.context.getString(R.string.status_booked)
                    binding.slotContainer.setBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.red_light)
                    )
                    binding.tvSlotStatus.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.red_dark)
                    )
                    binding.root.isEnabled = false
                }
                SlotStatus.COMPLETED -> {
                    binding.tvSlotStatus.text = binding.root.context.getString(R.string.status_completed)
                    binding.slotContainer.setBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.gray_light)
                    )
                    binding.tvSlotStatus.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.gray_dark)
                    )
                    binding.root.isEnabled = false
                }
                SlotStatus.CANCELLED -> {
                    binding.tvSlotStatus.text = binding.root.context.getString(R.string.status_cancelled)
                    binding.slotContainer.setBackgroundColor(
                        ContextCompat.getColor(binding.root.context, R.color.gray_light)
                    )
                    binding.tvSlotStatus.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.gray_dark)
                    )
                    binding.root.isEnabled = false
                }
            }
            
            binding.root.setOnClickListener {
                onSlotClick(slot)
            }
        }
    }
    
    class SlotDiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
        override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
            return oldItem == newItem
        }
    }
}
