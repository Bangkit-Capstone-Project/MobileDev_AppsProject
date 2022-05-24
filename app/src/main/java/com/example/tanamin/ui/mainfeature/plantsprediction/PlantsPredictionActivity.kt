package com.example.tanamin.ui.mainfeature.plantsprediction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.R

class PlantsPredictionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plants_prediction)

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