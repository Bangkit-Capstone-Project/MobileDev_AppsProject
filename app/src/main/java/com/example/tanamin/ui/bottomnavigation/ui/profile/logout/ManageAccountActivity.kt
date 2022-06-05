package com.example.tanamin.ui.bottomnavigation.ui.profile.logout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityManageAccountBinding
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.welcomingpage.WelcomingPageActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ManageAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageAccountBinding
    private lateinit var viewModel: ManageAccountViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()

        binding.cvLogout.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this@ManageAccountActivity, R.style.BottomSheetDialogTheme)
            val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.item_logout_dialog,
                findViewById<LinearLayout>(R.id.bottomSheet)
            )
            bottomSheetView.findViewById<View>(R.id.btn_logout).setOnClickListener {
                showLoading(true)
                viewModel.logout()
                startActivity(Intent(this, WelcomingPageActivity::class.java))
                finish()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[ManageAccountViewModel::class.java]

    }
    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE

    }
}