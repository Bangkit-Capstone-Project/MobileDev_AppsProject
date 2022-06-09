package com.example.tanamin.ui.bottomnavigation.ui.myplant

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentMyPlantBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.data.Diseases
import com.example.tanamin.nonui.data.News
import com.example.tanamin.nonui.response.AllDiseasesResponse
import com.example.tanamin.nonui.response.DiseasesItem
import com.example.tanamin.ui.alldesease.DeseaseAdapter
import com.example.tanamin.ui.history.HistoryActivity
import com.example.tanamin.ui.mainfeature.riceplant.RicePlantActivity
import com.example.tanamin.ui.news.DetailNewsActivity
import com.example.tanamin.ui.news.NewsActivity
import com.example.tanamin.ui.news.NewsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPlantFragment : Fragment() {

    private var _binding: FragmentMyPlantBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var rvNews: RecyclerView
    private val list = ArrayList<News>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyPlantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvNews = binding.rvNews
        rvNews.setHasFixedSize(true)
        rvNews.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        list.addAll(listNews)
        showRecyclerList()
        getDiseaseFromApi()




        binding.btnHistory.setOnClickListener {
            val intentToHistory = Intent(this@MyPlantFragment.requireContext(), HistoryActivity::class.java)
            startActivity(intentToHistory)
        }
        binding.tvAllNews.setOnClickListener {
            val allNews = Intent(this.requireContext(), NewsActivity::class.java)
            startActivity(allNews)
        }

        return root
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
                val newsDetail = Intent(this@MyPlantFragment.requireContext(), DetailNewsActivity::class.java)
                newsDetail.putExtra("DATA", data)
                startActivity(newsDetail)
            }
        })

    }
    private fun getDiseaseFromApi(){
        showLoading(true)
        val client = ApiConfig.getApiService().getAllDeseases()
        client.enqueue(object: Callback<AllDiseasesResponse> {
            override fun onResponse(call: Call<AllDiseasesResponse>, response: Response<AllDiseasesResponse>) {
                if(response.isSuccessful){
                    showLoading(false)
                    val responseBody = response.body()
                    if(responseBody != null){
                        getList(responseBody.dataDisease.diseases)
                        Log.d(this@MyPlantFragment.toString(), "onResponse: ${responseBody.dataDisease.diseases}")
                    }
                }
            }

            override fun onFailure(call: Call<AllDiseasesResponse>, t: Throwable) {
                showLoading(false)
                Log.d(this@MyPlantFragment.toString(), "onFailure: ${t.message}")
            }
        })
    }
    private fun getList(diseases: List<DiseasesItem>){
        val listDisease = ArrayList<Diseases>()
        for(disease in diseases){
            val diseaseItem = Diseases(
                disease.id,
                disease.name,
                disease.description,
                disease.imageUrl
            )
            listDisease.add(diseaseItem)
        }
        showRecyclerListArticle(listDisease)
    }

    private fun showRecyclerListArticle(listUser: ArrayList<Diseases>) {


            binding.rvDeseases.layoutManager = LinearLayoutManager(activity)


        val listUserAdapter = DeseaseAdapter(listUser)
        binding.rvDeseases.adapter = listUserAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE
    }
}