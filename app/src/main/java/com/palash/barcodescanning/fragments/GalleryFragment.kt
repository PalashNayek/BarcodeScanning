package com.palash.barcodescanning.fragments

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.palash.barcodescanning.R
import com.palash.barcodescanning.databinding.FragmentGalleryBinding
import com.palash.barcodescanning.view_models.GalleryBarcodeScannerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GalleryBarcodeScannerViewModel by viewModels()

    private val selectImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Use `this.contentResolver` instead of `contentResolver` for accessing the content
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            binding.imageView.setImageBitmap(bitmap)
            viewModel.scanBarcode(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectImageResultLauncher.launch("image/*")
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.barcodes.observe(viewLifecycleOwner, Observer { barcodes ->
            val barcodeData = barcodes.joinToString("\n") { it.rawValue ?: "No data" }
            //binding.resultTextView.text = barcodeData
            if (barcodes.isNotEmpty()){
                Log.d("My Scanning result: ", barcodeData)
                findNavController().navigate(R.id.action_GalleryFragment_to_dashFragment)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            //binding.resultTextView.text = errorMessage
            Log.d("My Scanning result: ", errorMessage)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}