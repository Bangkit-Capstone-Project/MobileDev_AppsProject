package com.example.tanamin.ui.welcomingpage

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityWelcomingPageBinding
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.bottomnavigation.BottomNavigationActivity
import com.example.tanamin.ui.login.LoginActivity
import com.example.tanamin.ui.login.LoginViewModel
import com.example.tanamin.ui.signup.SignupActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class WelcomingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomingPageBinding
    private lateinit var viewModel: WelcomingPageViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //ANIMATION
        playAnimation()

        //SESSION CHECKER
        setupViewModel()

        //BUTTON HANDLER
        binding.welcomingBtnLogin.setOnClickListener{
            val loginIntent = Intent(this@WelcomingPageActivity, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.welcomingBtnSignup.setOnClickListener{
            val SingupIntent = Intent(this@WelcomingPageActivity, SignupActivity::class.java)
            startActivity(SingupIntent)
        }
    }

    //ANIMATION
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.welcomingImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    //SESSION CHECKER
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[WelcomingPageViewModel::class.java]

        viewModel.getSession().observe(this) { session ->
            if (session) {
                val mainIntent = Intent(this, BottomNavigationActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }
    }

}