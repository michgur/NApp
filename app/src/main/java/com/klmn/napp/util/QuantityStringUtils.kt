package com.klmn.napp.util

import android.icu.text.DecimalFormat
import java.util.*

private val fixedQuantities = mapOf(
    "kcal" to (1f to "kcal"),
    "kj" to (.239006f to "kcal"),
    "mg" to (1f to "mg"),
    "g" to (1f to "g"),
    "kg" to (1f to "kg"),
    "ml" to (1f to "ml"),
    "l" to (1f to "L"),
    "cl" to (10f to "ml"),
    "oz" to (28.35f to "g"),
    "lb" to (454f to "g")
)

private val quantityFormat = DecimalFormat().apply {
    maximumFractionDigits = 2
}

fun formatQuantity(quantity: Float, divideK: Boolean = true): String =
    if (quantity < 1000 || !divideK) quantityFormat.format(quantity)
    else quantityFormat.format(quantity / 1000) + "k"

fun fixQuantity(quantity: String, unit: String) =
    fixedQuantities[unit.toLowerCase(Locale.ROOT)]?.let {
        quantity.toFloatOrNull()?.times(it.first) to it.second
    }

private val quantityUnitRegex = Regex("([0-9,.]+)\\s*([a-zA-Z]+).*")

fun extractQuantityAndUnit(string: String) = quantityUnitRegex.find(string)?.let {
    fixQuantity(it.groupValues[1].replace(',', '.'), it.groupValues[2])
}