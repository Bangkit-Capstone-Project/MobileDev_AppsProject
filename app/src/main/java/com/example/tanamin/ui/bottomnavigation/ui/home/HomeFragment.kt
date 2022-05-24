package com.example.tanamin.ui.bottomnavigation.ui.home


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.FragmentHomeBinding
import com.example.tanamin.ui.mainfeature.casavaplant.CassavaPlantActivity
import com.example.tanamin.ui.mainfeature.plantsprediction.PlantsPredictionActivity
import com.example.tanamin.ui.mainfeature.riceplant.RicePlantActivity


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvSlogan
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //HANDLING CARD VIEW FOR ALL THE MAINFEATURE
        //RICE
        binding.cvRice.setOnClickListener {
            requireActivity().run{
                startActivity(Intent(this, RicePlantActivity::class.java))
                finish()
            }
        }

        //CASSAVA
        binding.cvCassava.setOnClickListener {
            requireActivity().run{
                startActivity(Intent(this, CassavaPlantActivity::class.java))
                finish()
            }
        }

        //PLANTS PREDICTION
        binding.cvVegetable.setOnClickListener {
            requireActivity().run{
                startActivity(Intent(this, PlantsPredictionActivity::class.java))
                finish()
            }
        }

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}