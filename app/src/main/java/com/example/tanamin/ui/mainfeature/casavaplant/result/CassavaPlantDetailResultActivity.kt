package com.example.tanamin.ui.mainfeature.casavaplant.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityCassavaPlantDetailResultBinding
import com.example.tanamin.nonui.data.PlantsDiseases
import com.example.tanamin.ui.mainfeature.tomatoplant.result.TomatoPlantDetailResultActivity
import kotlin.math.roundToInt

class CassavaPlantDetailResultActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_RESULT = "extra_result"
    }

    private lateinit var binding: ActivityCassavaPlantDetailResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCassavaPlantDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.floatBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        val result = intent.getParcelableExtra<PlantsDiseases>(EXTRA_RESULT) as PlantsDiseases
        val roundoff = "${((result.accuracy * 100.0).roundToInt())}%"

        Glide.with(this).load(result.imageUrl).into(binding.imageResult)
        binding.nameResult.setText(result.plantName)
        binding.AccuracyResult.setText(roundoff)
        binding.CreatedAt.setText(result.createdAt)
        binding.DescriptionResult.setText(result.description)
        binding.HistoryId.setText(result.historyId)
        binding.DiseaseName.setText(result.diseasesName)

    }
}