package com.example.tanamin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.tanamin.databinding.ActivitySplashScreenctivityBinding
import com.example.tanamin.ui.bottomnavigation.BottomNavigationActivity
import com.example.tanamin.ui.welcomingpage.WelcomingPageActivity

class SplashScreenctivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenctivityBinding
    private val splashScreenTimeout : Long = 1500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenctivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            startActivity(Intent(this, WelcomingPageActivity::class.java))
            finish()
        }, splashScreenTimeout
        )
        supportActionBar?.hide()
    }
}