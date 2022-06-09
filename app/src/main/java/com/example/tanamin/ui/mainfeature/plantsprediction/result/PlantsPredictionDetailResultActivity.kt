package com.example.tanamin.ui.mainfeature.plantsprediction.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityPlantsPredictionBinding
import com.example.tanamin.databinding.ActivityPlantsPredictionDetailResultBinding
import com.example.tanamin.ui.mainfeature.plantsprediction.PlantsPredictionActivityViewModel

class PlantsPredictionDetailResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantsPredictionDetailResultBinding
    private lateinit var viewModel: PlantsPredictionDetailResultViewModel

    companion object{
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_ACCURACY = "extra_accuracy"
        const val EXTRA_IMAGEURL = "extra_imageurl"
        const val EXTRA_VEGETABLENAME  = "extra_vegetablename"
        const val EXTRA_CREATEDAT = "extra_createdat"
        const val TAG = "Bismillah cepet bisanya"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantsPredictionDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.floatBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val accuracy = intent.getStringExtra(EXTRA_ACCURACY)
        val imageUrl = intent.getStringExtra(EXTRA_IMAGEURL)
        val vegetableName = intent.getStringExtra(EXTRA_VEGETABLENAME)
        val createdAt = intent.getStringExtra(EXTRA_CREATEDAT)

        Log.d(TAG, "description: $description")
        Log.d(TAG, "accuracy: $accuracy")
        Log.d(TAG, "imageUrl: $imageUrl")
        Log.d(TAG, "vegetableName: $vegetableName")
        Log.d(TAG, "createdAt: $createdAt")

        Glide.with(this).load(imageUrl).into(binding.imageResult)
        binding.nameResult.setText(vegetableName)
        binding.AccuracyResult.setText(accuracy)
        binding.CreatedAt.setText(createdAt)
        binding.DescriptionResult.setText(description)
    }
}