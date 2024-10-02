package com.palash.barcodescanning.view_models

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.palash.barcodescanning.repositories.BarcodeScannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryBarcodeScannerViewModel @Inject constructor(
    private val repository: BarcodeScannerRepository
) : ViewModel() {

    private val _barcodes = MutableLiveData<List<Barcode>>()
    val barcodes: LiveData<List<Barcode>> get() = _barcodes

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun scanBarcode(bitmap: Bitmap) {
        viewModelScope.launch {
            repository.scanBarcode(
                bitmap,
                { barcodes ->
                    _barcodes.postValue(barcodes)
                },
                { exception ->
                    _error.postValue(exception.message)
                }
            )
        }
    }
}