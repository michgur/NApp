package com.klmn.napp.util

import android.graphics.Bitmap
import android.graphics.Matrix

/* rotate bitmap in multiples of 90, clockwise rotation */
fun Bitmap.rotate(rotation: Int): Bitmap = Bitmap.createBitmap(
    this, 0, 0, width, height,
    Matrix().apply { postRotate(rotation * 90f % 360) },
    true
)