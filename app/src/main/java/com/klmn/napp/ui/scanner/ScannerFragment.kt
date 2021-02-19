package com.klmn.napp.ui.scanner

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.AudioManager.STREAM_MUSIC
import android.media.ToneGenerator
import android.media.ToneGenerator.TONE_CDMA_PIP
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import androidx.core.view.doOnLayout
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.Barcode.ALL_FORMATS
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.klmn.napp.databinding.FragmentScannerBinding
import com.klmn.napp.scanner.RegionEnclosedDetector
import com.klmn.napp.scanner.encloseInRegion
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.makeToast

class ScannerFragment : ViewBoundFragment<FragmentScannerBinding>(FragmentScannerBinding::inflate),
    SurfaceHolder.Callback, Detector.Processor<Barcode> {
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var barcodeDetector: Detector<Barcode>
    private lateinit var cameraSource: CameraSource

    companion object { const val TAG = "ScannerFragment" }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = initScanner()

    private fun initScanner() = binding.root.doOnLayout {
        toneGenerator = ToneGenerator(STREAM_MUSIC, 100)
        barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(ALL_FORMATS)
            .build()
            .encloseInRegion(
                if (resources.configuration.orientation == ORIENTATION_PORTRAIT)
                    Rect(576, 108, 960, 972)
                else Rect(528, 324, 1392, 708)
            )
        cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()
        binding.surfaceView.holder.addCallback(this)
        barcodeDetector.setProcessor(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED)
                cameraSource.start(holder)
            else ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA), 1)
        } catch (e: Exception) {
            Log.d(TAG, e.stackTraceToString())
            e.message?.let(::makeToast)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) = cameraSource.stop()

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

    override fun release() = Unit

    override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        detections.detectedItems.takeIf { it.isNotEmpty() }?.valueAt(0)?.let { barcode ->
            Log.d(TAG, "barcode detected")
            Log.d(TAG, "cameraSource size=${cameraSource.previewSize}")
            Log.d(TAG, "frame size=${detections.frameMetadata.let { it.width to it.height }}")
            Log.d(TAG, "barcode value=${barcode.displayValue}")
            Log.d(TAG, "bounding box=${barcode.boundingBox.toShortString()}")
            binding.root.post {
                makeToast("barcode detected: ${barcode.displayValue}")
                cameraSource.stop()
            }
            toneGenerator.startTone(TONE_CDMA_PIP, 150)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        initScanner()
    }
}