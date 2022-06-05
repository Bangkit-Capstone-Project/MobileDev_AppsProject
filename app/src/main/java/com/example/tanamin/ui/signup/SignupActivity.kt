package com.example.tanamin.ui.signup

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.tanamin.databinding.ActivitySignupBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.RegisterResponse
import com.example.tanamin.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ANIMATION
        playAnimation()

        //Handling Backbutton
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        binding.signupBtnSignup.setOnClickListener{
            registerUser()
            showLoading(true)
        }
    }
    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE

    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //ANIMATION
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.signupImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun registerUser(){
        val email = binding.registerTextEditEmail.text.toString()
        val name = binding.registerTextEditName.text.toString()
        val userName = binding.registerTextEditUserName.text.toString()
        val password = binding.registerTextEditPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.registerTextInputEmail.error = "Masukkan Email"
                binding.registerTextInputEmail.requestFocus()
            }
            name.isEmpty() -> {
                binding.registerTextInputName.error = "Masukan Nama"
                binding.registerTextInputName.requestFocus()
            }
            userName.isEmpty() -> {
                binding.registerTextInputUserName.error = "Masukkan User Name"
                binding.registerTextInputUserName.requestFocus()
            }
            password.isEmpty() -> {
                binding.registerTextInputPassword.error = "Masukkan Password"
                binding.registerTextInputPassword.requestFocus()
            }
            else -> {
                showLoading(true)
                val client = ApiConfig.getApiService().registerUser(email, name, userName, password)

                client.enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        val responseBody = response.body()
                        showLoading(false)
                        if (response.isSuccessful) {
                            onToast("${responseBody?.message}")
                            Log.d(this@SignupActivity.toString(), response.message())
                            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        } else {
                            onToast("${responseBody?.message}")
                            Log.d(this@SignupActivity.toString(), response.message())
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        showLoading(false)
                        onToast("${t.message}")
                        Log.d(this@SignupActivity.toString(), "${t.message}")
                    }
                })
            }
        }
    }


    private fun onToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


}