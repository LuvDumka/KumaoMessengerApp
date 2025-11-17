package com.example.mentorconnect.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mentorconnect.R
import com.example.mentorconnect.databinding.ActivityMainBinding
import com.example.mentorconnect.ui.main.mentor.MentorListFragment
import com.example.mentorconnect.ui.main.profile.ProfileFragment
import com.example.mentorconnect.ui.main.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mentorFragment = MentorListFragment()
    private val profileFragment = ProfileFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_mentors -> switchFragment(mentorFragment)
                R.id.navigation_profile -> switchFragment(profileFragment)
                R.id.navigation_settings -> switchFragment(settingsFragment)
            }
            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.navigation_mentors
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
        binding.toolbar.title = when (fragment) {
            is MentorListFragment -> getString(R.string.label_mentors)
            is ProfileFragment -> getString(R.string.label_profile)
            is SettingsFragment -> getString(R.string.label_settings)
            else -> getString(R.string.app_name)
        }
    }
}
