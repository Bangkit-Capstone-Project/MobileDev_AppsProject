package com.example.tanamin.ui.riceplant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tanamin.databinding.ActivityRiceplantBinding

class RiceplantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiceplantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiceplantBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}