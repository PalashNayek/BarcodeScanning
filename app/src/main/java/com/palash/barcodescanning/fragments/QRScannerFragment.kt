package com.palash.barcodescanning.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.palash.barcodescanning.databinding.FragmentQRScannerBinding
import com.palash.barcodescanning.view_models.BarcodeScannerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.io.ByteArrayOutputStream
import android.graphics.ImageFormat
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.palash.barcodescanning.R
import java.nio.ByteBuffer

@AndroidEntryPoint
class QRScannerFragment : Fragment() {
    private var _binding: FragmentQRScannerBinding? = null
    private val binding get() = _binding!!

    /*private lateinit var cameraExecutor: ExecutorService

    */

    private val viewModel: BarcodeScannerViewModel by viewModels()

    private lateinit var cameraExecutor: ExecutorService
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start camera if permission granted
            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
        } else {
            // Handle permission denied
            Toast.makeText(requireContext(), "Please give camera permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQRScannerBinding.inflate(inflater, container, false)
        requestCameraPermission()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val scanLine = view.findViewById<View>(R.id.scanLine)



        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
        observeViewModel()
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val animator = ObjectAnimator.ofFloat(binding.scanLine, "translationY", 0f, binding.cameraPreview.height.toFloat())
        animator.duration = 2000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.start()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { bitmap ->
                        lifecycleScope.launch {
                            viewModel.scanBarcode(bitmap)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun observeViewModel() {
        viewModel.barcodes.observe(viewLifecycleOwner) { barcodes ->
            val result = barcodes.joinToString("\n") { it.rawValue ?: "No data" }
            binding.barcodeResult.text = result
            if (result.isNotEmpty()){
                findNavController().navigate(R.id.action_QRScannerFragment_to_dashFragment)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.barcodeResult.text = errorMessage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    private class BarcodeAnalyzer(private val listener: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val bitmap = imageProxy.toBitmap()
            listener(bitmap)
            imageProxy.close()
        }
    }
}
