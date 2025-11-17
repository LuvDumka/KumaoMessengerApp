package com.example.mentorconnect.ui.mentor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mentorconnect.R
import com.example.mentorconnect.databinding.ActivityMentorDashboardBinding

class MentorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMentorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMentorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(MentorChatsFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> {
                    loadFragment(MentorChatsFragment())
                    true
                }
                R.id.nav_video_calls -> {
                    loadFragment(MentorVideoCallsFragment())
                    true
                }
                R.id.nav_mentor_settings -> {
                    loadFragment(MentorSettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
