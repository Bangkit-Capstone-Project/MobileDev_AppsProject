package com.example.tanamin.ui.bottomnavigation.ui.profile.credit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tanamin.databinding.ActivityCreditBinding

class CreditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Handling Backbutton
        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }






}