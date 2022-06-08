package com.example.tanamin.ui.history

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityHistoryBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.data.Diseases
import com.example.tanamin.nonui.data.History
import com.example.tanamin.nonui.response.DiseasesItem
import com.example.tanamin.nonui.response.HistoryResponse
import com.example.tanamin.nonui.response.PredictionHistorysItem
import com.example.tanamin.nonui.response.RefreshTokenResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.alldesease.DeseaseAdapter
import com.example.tanamin.ui.history.detail.HistoryDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupModel()

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

    //GETTING EVERYTHING FOR THE API :)
    private fun setupModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[HistoryActivityViewModel::class.java]

        viewModel.getRefreshToken().observe(this){ userRefreshToken ->
            val refreshToken = userRefreshToken
            refreshTokenin(refreshToken)
        }

        viewModel.getToken().observe(this) { userToken ->
            var token = userToken
            getHistory(token)
        }
    }

    //TO REFRESH THE TOKEN
    private fun refreshTokenin(refreshToken: String){
        val service = ApiConfig.getApiService().getRefreshedToken(refreshToken)
        service.enqueue(object: Callback<RefreshTokenResponse> {
            override fun onResponse(
                call: Call<RefreshTokenResponse>,
                response: Response<RefreshTokenResponse>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful){
                    viewModel.saveToken(responseBody?.data!!.accessToken)
                }else{
                }
            }
            override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
            }

        })
    }

    //TO GET THE HISTORY DATA
    private fun getHistory(token: String){
        val theToken ="Bearer $token"
        showLoading(true)
        val service = ApiConfig.getApiService().getHistory(theToken)
        service.enqueue(object: Callback<HistoryResponse>{
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if(responseBody != null){
                    getList(responseBody.data.predictionHistorys)
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getList(History: List<PredictionHistorysItem>){
        val listHistory = ArrayList<History>()
        for(history in History){
            val historyItem = History(
                history.id,
                history.plantName,
                history.diseasesName,
                history.diseasesDescription,
                history.accuracy,
                history.imageUrl,
                history.createdAt
            )
            listHistory.add(historyItem)
        }
        showRecyclerList(listHistory)
    }

    private fun showRecyclerList(listHistory: ArrayList<History>) {

        if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.rvDeseases.layoutManager = GridLayoutManager(this , 2)
        }else {
            binding.rvDeseases.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        val listHistoryAdapter = HistoryAdapter(listHistory)
        binding.rvDeseases.adapter = listHistoryAdapter

        listHistoryAdapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: History) {


                val intentDetail = Intent(this@HistoryActivity, HistoryDetailActivity::class.java)
                intentDetail.putExtra(HistoryDetailActivity.EXTRA_DETAIL, listHistory)
                startActivity(intentDetail)
            }

        })
    }

    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE
    }
}