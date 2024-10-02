package com.palash.barcodescanning.view_models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor() : ViewModel() {

    private val _barcodes = MutableLiveData<List<Barcode>>()
    val barcodes: LiveData<List<Barcode>> get() = _barcodes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    suspend fun scanBarcode(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        withContext(Dispatchers.IO) {
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    _barcodes.postValue(barcodes)
                }
                .addOnFailureListener { exception ->
                    _error.postValue(exception.message)
                }
        }
    }
}