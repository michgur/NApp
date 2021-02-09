package com.klmn.napp.data.network.entities

object NetworkEntities {
    data class Product(
        val id: String? = null,
        val product_name: String? = null,
        val quantity: String? = null,
        val ingredients_analysis_tags: List<String>? = null,
        val image_small_url: String = "",
        @ProductLabel val nutriments: Nutriments? = null,
        @ProductLabel val brands: String? = null,
        @ProductLabel val categories: String? = null,
        @ProductLabel val packaging: String? = null,
        @ProductLabel val labels: String? = null,
        @ProductLabel val origins: String? = null,
        @ProductLabel val manufacturing_places: String? = null,
        @ProductLabel val emb_codes: String? = null,
        @ProductLabel val purchase_places: String? = null,
        @ProductLabel val stores: String? = null,
        @ProductLabel val countries: String? = null,
        @ProductLabel val additives: String? = null,
        @ProductLabel val allergens: String? = null,
        @ProductLabel val traces: String? = null,
        @ProductLabel val nutrition_grades: String? = null,
        @ProductLabel val states: String? = null
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