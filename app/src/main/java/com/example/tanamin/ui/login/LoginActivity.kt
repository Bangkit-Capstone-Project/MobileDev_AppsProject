package com.example.tanamin.ui.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityLoginBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.LoginResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.bottomnavigation.BottomNavigationActivity
import com.example.tanamin.ui.welcomingpage.WelcomingPageActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
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


        //SESSION CHECKER
        setupViewModel()

        //Handling Backbutton
        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


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
                showLoading(true)
                val service = ApiConfig.getApiService().loginUser(userName, password)
                service.enqueue(object: Callback<LoginResponse>{
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val responseBody = response.body()
                        showLoading(false)
                        if (response.isSuccessful) {

                            viewModel.saveToken(responseBody?.data!!.accessToken)
                            viewModel.saveRefreshToken(responseBody?.data!!.refreshToken)
                            Log.d(this@LoginActivity.toString(), "onResponse: ${responseBody?.data!!.refreshToken}")
                            Log.d(this@LoginActivity.toString(), "Token: ${responseBody?.data!!.accessToken}")
                            Log.d(this@LoginActivity.toString(),"${responseBody?.message}")
                            val bottomSheetDialog = BottomSheetDialog(this@LoginActivity, R.style.BottomSheetDialogTheme)
                            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                                R.layout.item_bottomsheet_login,
                                findViewById<LinearLayout>(R.id.bottomSheet)
                            )
                            bottomSheetDialog.setContentView(bottomSheetView)
                            bottomSheetDialog.show()
                            bottomSheetView.findViewById<View>(R.id.btn_next).setOnClickListener {
                                viewModel.login()
                                val next  = Intent(this@LoginActivity, BottomNavigationActivity::class.java)
                                startActivity(next)
                            }
                        } else {
                            Log.d(this@LoginActivity.toString(), response.message())
                            val bottomSheetDialog = BottomSheetDialog(this@LoginActivity, R.style.BottomSheetDialogTheme)
                            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
                                R.layout.item_bottomsheet_failed,
                                findViewById<LinearLayout>(R.id.bottomSheet)
                            )
                            bottomSheetDialog.setContentView(bottomSheetView)
                            bottomSheetDialog.show()
                            bottomSheetView.findViewById<View>(R.id.btn_tryagain).setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showLoading(false)
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



    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE
    }

}