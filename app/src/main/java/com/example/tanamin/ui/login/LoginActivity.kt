package com.example.tanamin.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.databinding.ActivityLoginBinding
import com.example.tanamin.ui.bottomnavigation.BottomNavigationActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        //Handling Backbutton
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        binding.loginBtnLogin.setOnClickListener {
            startActivity(Intent(this@LoginActivity, BottomNavigationActivity::class.java))
        }

    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}