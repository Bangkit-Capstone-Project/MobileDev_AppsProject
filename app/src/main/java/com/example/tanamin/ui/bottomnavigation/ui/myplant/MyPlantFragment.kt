package com.example.tanamin.ui.bottomnavigation.ui.myplant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.databinding.FragmentMyPlantBinding
import com.example.tanamin.ui.history.HistoryActivity
import com.example.tanamin.ui.mainfeature.riceplant.RicePlantActivity


class MyPlantFragment : Fragment() {

    private var _binding: FragmentMyPlantBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(MyPlantViewModel::class.java)

        _binding = FragmentMyPlantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvEmptyPlant
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }


        binding.historySection.setOnClickListener {
            val intentToHistory = Intent(this@MyPlantFragment.requireContext(), HistoryActivity::class.java)
            startActivity(intentToHistory)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}