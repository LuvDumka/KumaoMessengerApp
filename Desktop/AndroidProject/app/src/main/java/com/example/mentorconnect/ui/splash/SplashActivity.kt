package com.example.mentorconnect.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.mentorconnect.core.ServiceLocator
import com.example.mentorconnect.databinding.ActivitySplashBinding
import com.example.mentorconnect.ui.auth.LoginActivity
import com.example.mentorconnect.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textTagline.alpha = 0f
        binding.textTagline.animate().alpha(1f).setDuration(600).start()

        lifecycleScope.launch {
            delay(1600)
            val destination = if (ServiceLocator.isUserLoggedIn()) {
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, destination))
            finish()
        }
    }
}
