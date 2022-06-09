package com.example.tanamin.ui.bottomnavigation.ui.home


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentHomeBinding
import com.example.tanamin.nonui.data.News
import com.example.tanamin.ui.alldesease.AllDeseaseActivity
import com.example.tanamin.ui.bottomnavigation.ui.profile.ProfileFragment
import com.example.tanamin.ui.mainfeature.casavaplant.CassavaPlantActivity
import com.example.tanamin.ui.mainfeature.plantsprediction.CameraPlantsPredictionActivity
import com.example.tanamin.ui.mainfeature.plantsprediction.PlantsPredictionActivity
import com.example.tanamin.ui.mainfeature.riceplant.RicePlantActivity
import com.example.tanamin.ui.mainfeature.tomatoplant.TomatoPlantActivity
import com.example.tanamin.ui.news.DetailNewsActivity
import com.example.tanamin.ui.news.NewsActivity
import com.example.tanamin.ui.news.NewsAdapter


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)





        binding.apply {
            cvRice.setOnClickListener {
                val intentToRice = Intent(this@HomeFragment.requireContext(), RicePlantActivity::class.java)
                startActivity(intentToRice)
            }
            cvCassava.setOnClickListener {
                val intentToCassava = Intent(this@HomeFragment.requireContext(), CassavaPlantActivity::class.java)
                startActivity(intentToCassava)
            }
            cvVegetables.setOnClickListener {
                val intentToVegetable = Intent(this@HomeFragment.requireContext(), PlantsPredictionActivity::class.java)
                startActivity(intentToVegetable)
            }
            cvTomato.setOnClickListener {
                val intentToTomato = Intent(this@HomeFragment.requireContext(), TomatoPlantActivity::class.java)
                startActivity(intentToTomato)
            }

        }
    }









    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}