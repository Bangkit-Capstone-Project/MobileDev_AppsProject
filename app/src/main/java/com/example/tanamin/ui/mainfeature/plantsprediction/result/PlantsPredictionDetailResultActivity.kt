package com.example.tanamin.ui.mainfeature.plantsprediction.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityPlantsPredictionBinding
import com.example.tanamin.databinding.ActivityPlantsPredictionDetailResultBinding
import com.example.tanamin.nonui.data.Classification
import com.example.tanamin.nonui.data.History
import com.example.tanamin.ui.history.detail.HistoryDetailActivity
import com.example.tanamin.ui.mainfeature.plantsprediction.PlantsPredictionActivityViewModel
import kotlin.math.roundToInt

class PlantsPredictionDetailResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantsPredictionDetailResultBinding

    companion object{
        const val EXTRA_RESULT = "extra_result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantsPredictionDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.floatBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        val result = intent.getParcelableExtra<Classification>(EXTRA_RESULT) as Classification
        val roundoff = ((result.accuracy.toDouble() * 100.0).roundToInt()).toString()

        Glide.with(this).load(result.imageUrl).into(binding.imageResult)
        binding.nameResult.setText(result.vegetableName)
        binding.AccuracyResult.setText("${roundoff}%")
        binding.CreatedAt.setText(result.createdAt)
        binding.DescriptionResult.setText(result.description)
    }
}