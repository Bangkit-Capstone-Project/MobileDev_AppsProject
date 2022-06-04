package com.example.tanamin.ui.alldesease

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityAllDeseaseBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.api.ApiService
import com.example.tanamin.nonui.response.AllDeseaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllDeseaseActivity : AppCompatActivity() {
    private lateinit var adapter: DeseaseAdapter
    private lateinit var binding: ActivityAllDeseaseBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDeseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DeseaseAdapter()
        adapter.notifyDataSetChanged()


        binding.apply {
            rvDeseases.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            rvDeseases.setHasFixedSize(true)
            rvDeseases.adapter = adapter
        }
        getDataFromApi()






    }
    private fun getDataFromApi(){
        showLoading(true)
        ApiConfig.getApiService().getAllDeseases()
            .enqueue(object : Callback<AllDeseaseResponse> {
                override fun onFailure(call: Call<AllDeseaseResponse>, t: Throwable) {
                    printLog( t.toString() )
                    showLoading(false)
                }
                override fun onResponse(
                    call: Call<AllDeseaseResponse>,
                    response: Response<AllDeseaseResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        showResult( response.body()!! )
                    }
                }
            })
    }
    private fun printLog(message: String) {
        Log.d(TAG, message)
    }
    private fun showResult(listDeseases: AllDeseaseResponse) {
        for (diseases in listDeseases.diseases) printLog( "title: ${diseases.name}" )
        adapter.setListDesease(listDeseases.diseases)
    }





    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE

    }

}