package com.example.tanamin.ui.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityNewsBinding
import com.example.tanamin.nonui.data.News

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var rvNews: RecyclerView
    private val list = ArrayList<News>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvNews = binding.rvNews
        rvNews.setHasFixedSize(true)
        rvNews.layoutManager = LinearLayoutManager(this)
        list.addAll(listNews)
        showRecyclerList()

        //Handling Backbutton
        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    private val listNews: ArrayList<News> get() {
        val dataNewsTitle = resources.getStringArray(R.array.news_tittle)
        val dataNewsDate = resources.getStringArray(R.array.news_date)
        val dataImageNews = resources.obtainTypedArray(R.array.news_image)
        val dataNewsDesc = resources.getStringArray(R.array.news_desc)

        val listNews = ArrayList<News>()
        for (i in dataNewsTitle.indices){
            val news = News(dataImageNews.getResourceId(i,-1), dataNewsTitle[i], dataNewsDate[i], dataNewsDesc[i])
            listNews.add(news)
        }
        return listNews
    }
    private fun showRecyclerList(){
        val newsAdapter = NewsAdapter(list)
        rvNews.adapter = newsAdapter
        newsAdapter.setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback{
            override fun onItemClicked(data: News) {
                val newsDetail = Intent(this@NewsActivity, DetailNewsActivity::class.java)
                newsDetail.putExtra("DATA", data)
                startActivity(newsDetail)
            }
        })

    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}