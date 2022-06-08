package com.example.tanamin.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivitySignupBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.RegisterResponse
import com.example.tanamin.ui.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
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


        //Handling Backbutton
        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

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


    //ANIMATION


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

                            Log.d(this@SignupActivity.toString(), response.message())
                            val bottomSheetDialog = BottomSheetDialog(this@SignupActivity, R.style.BottomSheetDialogTheme)
                            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                                R.layout.item_bottomsheet_register,
                                findViewById<LinearLayout>(R.id.bottomSheet)
                            )
                            bottomSheetDialog.setContentView(bottomSheetView)
                            bottomSheetDialog.show()
                            bottomSheetView.findViewById<View>(R.id.btn_next).setOnClickListener {
                                val next  = Intent(this@SignupActivity, LoginActivity::class.java)
                                startActivity(next)
                            }
                        } else {

                            val bottomSheetDialog = BottomSheetDialog(this@SignupActivity, R.style.BottomSheetDialogTheme)
                            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                                R.layout.item_bottomsheet_failed,
                                findViewById<LinearLayout>(R.id.bottomSheet)
                            )
                            bottomSheetDialog.setContentView(bottomSheetView)
                            bottomSheetDialog.show()
                            bottomSheetView.findViewById<View>(R.id.btn_tryagain).setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }

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