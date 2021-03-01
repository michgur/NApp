package com.klmn.napp.ui.scanner

import android.Manifest.permission.CAMERA
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager.FEATURE_CAMERA_FRONT
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Rect
import android.hardware.camera2.CameraManager
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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.Barcode.ALL_FORMATS
import com.google.android.gms.vision.barcode.Barcode.PRODUCT
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.klmn.napp.R
import com.klmn.napp.databinding.FragmentScannerBinding
import com.klmn.napp.scanner.encloseInRegion
import com.klmn.napp.ui.scanner.ScannerViewModel.State.*
import com.klmn.napp.util.ViewBoundFragment
import com.klmn.napp.util.makeToast
import com.klmn.napp.util.resolveColorAttribute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ScannerFragment : ViewBoundFragment<FragmentScannerBinding>(FragmentScannerBinding::inflate),
    SurfaceHolder.Callback, Detector.Processor<Barcode> {
    private lateinit var toneGenerator: ToneGenerator
    private lateinit var barcodeDetector: Detector<Barcode>
    private lateinit var cameraSource: CameraSource
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var flashAvailable = false

    companion object { const val TAG = "ScannerFragment" }

    private val viewModel: ScannerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initScanner()
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect(::setState)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.errors.collect { e ->
                e.message?.let(::makeToast)
            }
        }

        binding.toolbar.apply {
            setupWithNavController(findNavController())
            setNavigationIcon(R.drawable.ic_back)
            menu.findItem(R.id.action_flash)?.setOnMenuItemClickListener {
                toggleFlash()
                it.setIcon(
                    if (viewModel.flashEnabled) R.drawable.ic_flash_off
                    else R.drawable.ic_flash_on
                )
                true
            }
            menu.findItem(R.id.action_edit)?.setOnMenuItemClickListener {
                navToManualDialog()
                true
            }
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("barcode")?.observe(viewLifecycleOwner) { barcode ->
                barcode.toLongOrNull()?.let(viewModel::barcode::set)
            }
    }

    private fun navToManualDialog() = findNavController().navigate(
        ScannerFragmentDirections.actionScannerFragmentToInsertBarcodeDialog()
    )

    private fun toggleFlash() {
        viewModel.flashEnabled = !viewModel.flashEnabled
        if (flashAvailable) try {
            cameraManager.setTorchMode(cameraId, viewModel.flashEnabled)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

    private fun promptText(prompt: String) { binding.promptChip.text = prompt }

    private fun setState(state: ScannerViewModel.State) = when (state) {
        SCANNING -> {
            binding.scanAnimationView.isVisible = true
            binding.scanLoadingView.isVisible = false

            if (surfaceCreated) {
                startCamera()
                promptText(getString(R.string.scanner_point))
            } else Unit
        }
        LOOKUP -> {
            binding.scanAnimationView.isVisible = false
            binding.scanLoadingView.isVisible = true

            promptText(getString(R.string.scanner_detected, viewModel.barcode))
            cameraSource.stop()
        }
        SUCCESS -> {
            promptText(getString(R.string.scanner_success))
            findNavController().navigate(
                ScannerFragmentDirections.actionScannerFragmentToDetailsFragment(viewModel.barcode)
            )
            viewModel.barcode = 0L
        }
    }

    private fun initScanner() = binding.root.doOnLayout {
        toneGenerator = ToneGenerator(STREAM_MUSIC, 100)
        barcodeDetector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(ALL_FORMATS)
            .build()
            .encloseInRegion(
                // hard-coded values since the camera preview size is always the same
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
        flashAvailable = requireContext().packageManager.hasSystemFeature(FEATURE_CAMERA_FRONT)
        if (flashAvailable) {
            cameraManager = requireActivity().getSystemService(CAMERA_SERVICE) as CameraManager
            try { cameraId = cameraManager.cameraIdList[0] }
            catch (e: Exception) {
                Log.e(TAG, e.stackTraceToString())
                flashAvailable = false
            }
        }
    }

    private var surfaceCreated = false
    private fun startCamera() {
        surfaceCreated = true
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED)
                cameraSource.start(binding.surfaceView.holder)
            else ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA), 1)
        } catch (e: Exception) {
            Log.d(TAG, e.stackTraceToString())
            e.message?.let(::promptText)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) = startCamera()

    override fun surfaceDestroyed(holder: SurfaceHolder) = cameraSource.stop()

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

    override fun release() = Unit

    override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        detections.detectedItems.takeIf { it.isNotEmpty() }?.valueAt(0)?.let { barcode ->
            Log.d(TAG, "barcode detected (format=${barcode.valueFormat}, value=${barcode.displayValue})")
            if (barcode.valueFormat == PRODUCT)
                barcode.displayValue.toLongOrNull()?.let { value ->
                    viewModel.barcode = value
                    toneGenerator.startTone(TONE_CDMA_PIP, 150)
                }
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.state.value == SCANNING) initScanner()
    }
}