package com.klmn.napp.model

data class Nutriments(
    val carbohydrates_100g: Float,
    val carbohydrates_unit: String = "g",
    val energy: Int,
    val energy_unit: String = "kcal",
    val fat_100g: Float,
    val fat_unit: String = "g",
    val proteins_100g: Float,
    val proteins_unit: String = "g",
)