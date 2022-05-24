package com.example.tanamin.ui.mainfeature.riceplant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.databinding.ActivityRicePlantBinding

class RicePlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRicePlantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRicePlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Handling Backbutton
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}