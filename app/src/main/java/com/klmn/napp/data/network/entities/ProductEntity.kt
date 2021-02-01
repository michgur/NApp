package com.klmn.napp.data.network.entities

data class ProductEntity(
    val product_name: String? = null,
    val quantity: String? = null,
    val ingredients_analysis_tags: List<String>? = null,
    val image_small_url: String = "",
    val nutriments: NutrimentsEntity = NutrimentsEntity()
)