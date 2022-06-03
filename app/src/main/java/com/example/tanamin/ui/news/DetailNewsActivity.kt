package com.example.tanamin.ui.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityDetailNewsBinding
import com.example.tanamin.nonui.data.News

class DetailNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<News>("DATA")
        Log.d("Detail Data", data?.newsTittle.toString())

        val imgNews = findViewById<ImageView>(R.id.imgNews)
        val tvNewsTittle = findViewById<TextView>(R.id.tvNewsTittle)
        val tvNewsDate = findViewById<TextView>(R.id.tvNewsDate)
        val tvNewsDesc = findViewById<TextView>(R.id.tvNewsDesc)

        imgNews.setImageResource(data?.imgItem!!)
        tvNewsTittle.text = data.newsTittle
        tvNewsDate.text = data.newsDate
        tvNewsDesc.text = data.newsDesc

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