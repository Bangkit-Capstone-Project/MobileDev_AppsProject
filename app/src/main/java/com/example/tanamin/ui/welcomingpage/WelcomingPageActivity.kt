package com.example.tanamin.ui.welcomingpage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityWelcomingPageBinding
import com.example.tanamin.ui.login.LoginActivity
import com.example.tanamin.ui.signup.SignupActivity

class WelcomingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.welcomingBtnLogin.setOnClickListener{
            val loginIntent = Intent(this@WelcomingPageActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.welcomingBtnSignup.setOnClickListener{
            val SingupIntent = Intent(this@WelcomingPageActivity, SignupActivity::class.java)
            startActivity(SingupIntent)
        }
    }
}