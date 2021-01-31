package com.klmn.napp.model

data class Product(
    val product_name: String = "",
    val quantity: String = "",
    val nutriments: Nutriments = Nutriments()
)
