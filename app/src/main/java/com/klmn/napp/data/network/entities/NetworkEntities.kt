package com.klmn.napp.data.network.entities

object NetworkEntities {
    data class Product(
        val product_name: String? = null,
        val quantity: String? = null,
        val ingredients_analysis_tags: List<String>? = null,
        val image_small_url: String = "",
        val nutriments: Nutriments? = null
    )

    data class Nutriments(
        val energy: String = "0",
        val energy_unit: String = "kcal",
        val carbohydrates_100g: String = "0",
        val carbohydrates_unit: String = "g",
        val proteins_100g: String = "0",
        val proteins_unit: String = "g",
        val fat_100g: String = "0",
        val fat_unit: String = "g",
    )

    data class Search(
        val page: Int = 0,
        val page_size: Int = 0,
        val count: Int = 0,
        val products: List<Product> = listOf()
    )

    data class PixabaySearch(
        val hits: List<PixabayImage>
    )

    data class PixabayImage(
        val webformatURL: String
    )
}