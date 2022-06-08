package com.example.tanamin.ui.mainfeature.tomatoplant.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityTomatoPlantDetailResultBinding
import com.example.tanamin.ui.mainfeature.plantsprediction.result.PlantsPredictionDetailResultActivity

class TomatoPlantDetailResultActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_PLANTNAME = "extra_plantname"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_ACCURACY = "extra_accuracy"
        const val EXTRA_IMAGEURL = "extra_imageurl"
        const val EXTRA_HISTORYID = "extra_historyid"
        const val EXTRA_DISEASENAME  = "extra_diseasename"
        const val EXTRA_CREATEDAT = "extra_createdat"
        const val TAG = "Bismillah cepet bisanya"
    }

    private lateinit var binding: ActivityTomatoPlantDetailResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTomatoPlantDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val accuracy = intent.getStringExtra(EXTRA_ACCURACY)
        val imageUrl = intent.getStringExtra(EXTRA_IMAGEURL)
        val historyId = intent.getStringExtra(EXTRA_HISTORYID)
        val createdAt = intent.getStringExtra(EXTRA_CREATEDAT)
        val diseaseName = intent.getStringExtra(EXTRA_DISEASENAME)
        val plantName = intent.getStringExtra(EXTRA_PLANTNAME)

        Glide.with(this).load(imageUrl).into(binding.imageResult)
        binding.nameResult.setText(plantName)
        binding.AccuracyResult.setText(accuracy)
        binding.CreatedAt.setText(createdAt)
        binding.DescriptionResult.setText(description)
        binding.HistoryId.setText(historyId)
        binding.DiseaseName.setText(diseaseName)


    }
}