package com.example.mentorconnect.ui.main.mentor

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mentorconnect.data.model.Mentor
import com.example.mentorconnect.databinding.ItemMentorBinding
import com.example.mentorconnect.ui.booking.MentorDetailActivity
import com.example.mentorconnect.util.AvatarGenerator

class MentorAdapter : ListAdapter<Mentor, MentorAdapter.MentorViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMentorBinding.inflate(inflater, parent, false)
        return MentorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MentorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MentorViewHolder(private val binding: ItemMentorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mentor: Mentor) {
            binding.textName.text = mentor.name
            binding.textExpertise.text = mentor.expertise
            binding.textBio.text = mentor.bio
            
            // Generate default avatar based on first letter of name
            val avatar = AvatarGenerator.generateAvatar(mentor.name, binding.root.context)
            if (!mentor.avatarUrl.isNullOrBlank()) {
                Glide.with(binding.imageAvatar)
                    .load(mentor.avatarUrl)
                    .placeholder(avatar)
                    .error(avatar)
                    .into(binding.imageAvatar)
            } else {
                binding.imageAvatar.setImageDrawable(avatar)
            }
            
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, MentorDetailActivity::class.java)
                intent.putExtra(MentorDetailActivity.EXTRA_MENTOR, mentor)
                context.startActivity(intent)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Mentor>() {
        override fun areItemsTheSame(oldItem: Mentor, newItem: Mentor): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Mentor, newItem: Mentor): Boolean = oldItem == newItem
    }
}
