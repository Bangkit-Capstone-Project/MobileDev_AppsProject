package com.example.tanamin.ui.mainfeature.riceplant.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityRicePlantDetailResultBinding
import com.example.tanamin.nonui.data.PlantsDiseases
import com.example.tanamin.ui.mainfeature.tomatoplant.result.TomatoPlantDetailResultActivity
import kotlin.math.roundToInt

class RicePlantDetailResultActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_RESULT = "extra_result"
    }

     private lateinit var binding: ActivityRicePlantDetailResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRicePlantDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()
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