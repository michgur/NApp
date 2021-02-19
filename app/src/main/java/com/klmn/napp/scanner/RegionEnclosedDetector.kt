package com.klmn.napp.scanner

import android.graphics.*
import android.util.SparseArray
import android.view.Surface.ROTATION_90
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.klmn.napp.util.rotate
import java.io.ByteArrayOutputStream


class RegionEnclosedDetector<T>(
    private val delegate: Detector<T>,
    private val region: Rect
) : Detector<T>() {
    private fun cropFrame(frame: Frame): Frame {
        val byteArrayOutputStream = ByteArrayOutputStream()
        YuvImage(
            frame.grayscaleImageData?.array(),
            ImageFormat.NV21,
            frame.metadata.width,
            frame.metadata.height,
            null
        ).compressToJpeg(region, 100, byteArrayOutputStream)
        val bitmap = byteArrayOutputStream.toByteArray().let { jpeg ->
            BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
        }
        return Frame.Builder()
            .setBitmap(bitmap)
            .setRotation(frame.metadata.rotation)
            .build()
    }

    fun copy(region: Rect = this.region) = RegionEnclosedDetector(delegate, region)
    init {
        if (delegate is RegionEnclosedDetector)
            throw IllegalArgumentException("cannot nest RegionEnclosedDetectors")
    }

    override fun isOperational() = delegate.isOperational
    override fun release() = delegate.release()
    override fun setProcessor(processor: Processor<T>) = delegate.setProcessor(processor)
    override fun setFocus(id: Int) = delegate.setFocus(id)

    override fun detect(frame: Frame): SparseArray<T> = delegate.detect(cropFrame(frame))
    override fun receiveFrame(frame: Frame) = delegate.receiveFrame(cropFrame(frame))
}

fun <T> Detector<T>.encloseInRegion(region: Rect) =
    if (this is RegionEnclosedDetector) copy(region)
    else RegionEnclosedDetector(this, region)
