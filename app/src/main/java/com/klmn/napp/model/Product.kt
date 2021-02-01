package com.klmn.napp.model

data class Product(
    val name: String,
    val quantity: Int,
    val unit: String,
    val image_url: String,
    val vegan: Boolean,
    val nutriments: Nutriments
)
