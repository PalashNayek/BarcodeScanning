package com.palash.barcodescanning.repositories

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

class BarcodeScannerRepository @Inject constructor(
    private val barcodeScanner: BarcodeScanner
) {

    fun scanBarcode(bitmap: Bitmap, callback: (List<Barcode>) -> Unit, onError: (Exception) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                callback(barcodes)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}