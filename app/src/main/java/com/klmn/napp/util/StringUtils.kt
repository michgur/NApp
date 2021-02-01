package com.klmn.napp.util

import android.icu.text.DecimalFormat

private val quantityFormat = DecimalFormat().apply {
    maximumFractionDigits = 2
}

fun formatQuantity(quantity: Float): String =
    if (quantity < 1000) quantityFormat.format(quantity)
    else quantityFormat.format(quantity / 1000) + "k"