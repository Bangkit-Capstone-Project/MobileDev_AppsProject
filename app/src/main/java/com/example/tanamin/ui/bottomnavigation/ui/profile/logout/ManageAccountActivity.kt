package com.example.tanamin.ui.bottomnavigation.ui.profile.logout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityManageAccountBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.DeleteRefreshTokenResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.welcomingpage.WelcomingPageActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ManageAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageAccountBinding
    private lateinit var viewModel: ManageAccountViewModel
    private lateinit var refreshToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()

        //Handling Backbutton
        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        


        binding.cvLogout.setOnClickListener { beautifulUi() }
    }

    //TO GET THE REFRESH TOKEN KEY
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[ManageAccountViewModel::class.java]

        viewModel.getRefreshToken().observe(this){ userRefreshToken ->
            refreshToken = userRefreshToken
            Log.d(this@ManageAccountActivity.toString(), "refreshToken: $refreshToken")
        }
    }

    //TO DELETE THE REFRESH TOKEN WHEN LOGING OUT
    private fun deleteRefreshToken(){
        val service = ApiConfig.getApiService().deleteRefreshToken(refreshToken)
        Log.d(this@ManageAccountActivity.toString(), "refreshToken deleteRefreshToken: $refreshToken")
        service.enqueue(object: Callback<DeleteRefreshTokenResponse> {
            override fun onResponse(
                call: Call<DeleteRefreshTokenResponse>,
                response: Response<DeleteRefreshTokenResponse>
            ) {
                val responseBody = response.body()
                Log.d(this@ManageAccountActivity.toString(), "responseBody: $responseBody")
                if (responseBody != null) {
                    Toast.makeText(this@ManageAccountActivity, "${responseBody.status}", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@ManageAccountActivity, "Sorry, Failed to refresh token", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteRefreshTokenResponse>, t: Throwable) {
                Log.d(this@ManageAccountActivity.toString(), "Message if onFailure ${t.message}")
            }
        })
    }


    private fun beautifulUi(){
        deleteRefreshToken()
        val bottomSheetDialog = BottomSheetDialog(this@ManageAccountActivity, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.item_logout_dialog,
            findViewById<LinearLayout>(R.id.bottomSheet)
        )
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetView.findViewById<View>(R.id.btn_logout).setOnClickListener {
            deleteRefreshToken()
            viewModel.logout()
            startActivity(Intent(this, WelcomingPageActivity::class.java))
            finish()
        }
    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}