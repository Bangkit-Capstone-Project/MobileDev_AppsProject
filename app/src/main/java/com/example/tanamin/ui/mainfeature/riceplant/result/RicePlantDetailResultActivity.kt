package com.example.tanamin.ui.mainfeature.riceplant.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityRicePlantDetailResultBinding
import com.example.tanamin.ui.mainfeature.tomatoplant.result.TomatoPlantDetailResultActivity

class RicePlantDetailResultActivity : AppCompatActivity() {

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
     private lateinit var binding: ActivityRicePlantDetailResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRicePlantDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val description = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_DESCRIPTION)
        val accuracy = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_ACCURACY)
        val imageUrl = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_IMAGEURL)
        val historyId = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_HISTORYID)
        val createdAt = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_CREATEDAT)
        val diseaseName = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_DISEASENAME)
        val plantName = intent.getStringExtra(TomatoPlantDetailResultActivity.EXTRA_PLANTNAME)

        Glide.with(this).load(imageUrl).into(binding.imageResult)
        binding.nameResult.setText(plantName)
        binding.AccuracyResult.setText(accuracy)
        binding.CreatedAt.setText(createdAt)
        binding.DescriptionResult.setText(description)
        binding.HistoryId.setText(historyId)
        binding.DiseaseName.setText(diseaseName)
    }
}