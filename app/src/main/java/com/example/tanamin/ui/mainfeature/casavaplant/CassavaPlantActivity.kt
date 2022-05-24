package com.example.tanamin.ui.mainfeature.casavaplant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.R

class CassavaPlantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cassava_plant)

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