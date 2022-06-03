package com.example.tanamin.ui.bottomnavigation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.databinding.FragmentProfileBinding
import com.example.tanamin.ui.bottomnavigation.ui.profile.credit.CreditActivity
import com.example.tanamin.ui.mainfeature.casavaplant.CassavaPlantActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvName
        viewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        //HANDLIND TO CREDIT ACTIVITY
        binding.credit.setOnClickListener {
            requireActivity().run{
                startActivity(Intent(this, CreditActivity::class.java))
            }
        }

        //TO LOG OUT
        binding.btnLogout.setOnClickListener{


        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}