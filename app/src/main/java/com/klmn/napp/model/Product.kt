package com.klmn.napp.model

data class Product(
    val product_name: String = "",
    val quantity: String = "",
    val image_small_url: String = "",
    val nutriments: Nutriments = Nutriments()
)
