package com.klmn.napp.model

data class Product(
    val name: String,
    val quantity: Int,
    val image_small_url: String,
    val nutriments: Nutriments
)
