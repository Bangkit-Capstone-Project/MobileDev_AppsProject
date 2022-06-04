package com.example.tanamin.ui.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.databinding.ActivityLoginBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.LoginResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.bottomnavigation.BottomNavigationActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)


        //ANIMATION
        playAnimation()

        //SESSION CHECKER
        setupViewModel()

        //Handling Backbutton
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        binding.loginBtnLogin.setOnClickListener {
            loginUser()
        }
    }

    //SESSION CHECKER
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]

        viewModel.getSession().observe(this) { session ->
            if (session) {
                val mainIntent = Intent(this, BottomNavigationActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }
    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //ANIMATION
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    //LOGIN LOGIC
    private fun loginUser(){
        val userName = binding.loginTextEditUserName.text.toString()
        val password = binding.loginTextEditPassword.text.toString()

        when {
            userName.isEmpty() -> {
                binding.loginTextEditUserName.error = "Please input Your User Name"
                binding.loginTextEditUserName.requestFocus()
            }
            password.isEmpty() -> {
                binding.loginTextEditPassword.error = "Please input your Password"
                binding.loginTextEditPassword.requestFocus()
            }
            else -> {
                val service = ApiConfig.getApiService().loginUser(userName, password)
                service.enqueue(object: Callback<LoginResponse>{
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val responseBody = response.body()
                        if (response.isSuccessful) {
                            viewModel.saveToken(responseBody?.data!!.accessToken)
                            Log.d(this@LoginActivity.toString(),"${responseBody?.message}")

                            sendIntent(responseBody?.message.toString(), userName)
                        } else {
                            onToast("${responseBody?.message}")
                            Log.d(this@LoginActivity.toString(), response.message())
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        onToast("${t.message}")
                        Log.d(this@LoginActivity.toString(), "${t.message}")
                    }
                })
            }
        }
    }

    private fun onToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun sendIntent(msg: String, userName: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("$msg")
            setPositiveButton("Next") { _, _ ->
                val homeIntent = Intent(this@LoginActivity, BottomNavigationActivity::class.java)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                homeIntent.putExtra(BottomNavigationActivity.EXTRA_USERNAME, userName)
                Log.d(this@LoginActivity.toString(), "sendIntent: $userName")
                startActivity(homeIntent)
                viewModel.login()
                finish()
            }
            create()
            show()
        }
    }

}