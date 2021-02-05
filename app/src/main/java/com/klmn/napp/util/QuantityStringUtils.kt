package com.klmn.napp.util

import android.icu.text.DecimalFormat
import java.util.*

private val fixedQuantities = mapOf(
    "kcal" to (1f to "kcal"),
    "kj" to (4.184f to "kcal"),
    "mg" to (1f to "mg"),
    "g" to (1f to "g"),
    "kg" to (1f to "kg"),
    "ml" to (1f to "ml"),
    "l" to (1f to "l"),
    "cl" to (.1f to "ml")
)

private val quantityFormat = DecimalFormat().apply {
    maximumFractionDigits = 2
}

fun formatQuantity(quantity: Float): String =
    if (quantity < 1000) quantityFormat.format(quantity)
    else quantityFormat.format(quantity / 1000) + "k"

fun fixQuantity(quantity: String, unit: String) =
    fixedQuantities[unit.toLowerCase(Locale.ROOT)]?.let {
        quantity.toFloatOrNull()?.div(it.first) to it.second
    }

private val quantityUnitRegex = Regex("([0-9.]+)\\s*([a-zA-Z]+).*")

fun extractQuantityAndUnit(string: String) = quantityUnitRegex.find(string)?.let {
    fixQuantity(it.groupValues[1], it.groupValues[2])
}