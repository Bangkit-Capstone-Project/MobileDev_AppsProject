package com.example.tanamin.ui.mainfeature.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityMainFeatureBinding

class MainFeatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainFeatureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}