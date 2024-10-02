package com.palash.barcodescanning.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.palash.barcodescanning.R
import com.palash.barcodescanning.databinding.FragmentDashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashFragment : Fragment() {
    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gallery.setOnClickListener {
            findNavController().navigate(R.id.action_dashFragment_to_GalleryFragment)
        }

        binding.camera.setOnClickListener {
            findNavController().navigate(R.id.action_dashFragment_to_QRScannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}