package com.example.tanamin.ui.history.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityHistoryDetailBinding
import com.example.tanamin.nonui.data.History

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.floatBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        val detail = intent.getParcelableExtra<History>(EXTRA_DETAIL) as History

        Glide.with(this).load(detail.imageUrl).into(binding.imageResult)
        binding.nameResult.setText(detail.plantName)
        binding.AccuracyResult.setText(detail.accuracy)
        binding.CreatedAt.setText(detail.createdAt)
        binding.DescriptionResult.setText(detail.diseasesDescription)
        binding.HistoryId.setText(detail.id)
        binding.DiseaseName.setText(detail.diseasesName)
    }
}