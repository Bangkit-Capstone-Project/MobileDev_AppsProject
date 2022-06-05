package com.example.tanamin.ui.alldesease

import android.content.ContentValues.TAG
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tanamin.databinding.ActivityAllDeseaseBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.data.Diseases
import com.example.tanamin.nonui.response.AllDiseasesResponse
import com.example.tanamin.nonui.response.DiseasesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllDeseaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDeseaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDeseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDiseaseFromApi()

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

    private fun getDiseaseFromApi(){
        showLoading(true)
        val client = ApiConfig.getApiService().getAllDeseases()
        client.enqueue(object: Callback<AllDiseasesResponse>{
            override fun onResponse(call: Call<AllDiseasesResponse>, response: Response<AllDiseasesResponse>) {
                if(response.isSuccessful){
                    showLoading(false)
                    val responseBody = response.body()
                    if(responseBody != null){
                        getList(responseBody.dataDisease.diseases)
                        Log.d(this@AllDeseaseActivity.toString(), "onResponse: ${responseBody.dataDisease.diseases}")
                    }
                }
            }
            override fun onFailure(call: Call<AllDiseasesResponse>, t: Throwable) {
                Log.d(this@AllDeseaseActivity.toString(), "onFailure: ${t.message}")
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
        showRecyclerList(listDisease)
    }

    private fun showRecyclerList(listUser: ArrayList<Diseases>) {

        if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.rvDeseases.layoutManager = GridLayoutManager(this , 2)
        }else {
            binding.rvDeseases.layoutManager = LinearLayoutManager(this)
        }

        val listUserAdapter = DeseaseAdapter(listUser)
        binding.rvDeseases.adapter = listUserAdapter
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    private fun showLoading(isLoading:Boolean){ binding.progressBar.visibility =
        if (isLoading) View.VISIBLE
        else View.GONE

    }

}