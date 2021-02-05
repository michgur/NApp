package com.klmn.napp.model

data class Product(
    /* name of the product */
    val name: String,
    /* quantity of food in a product, measured in unit */
    val quantity: Float,
    /* measurement unit of food */
    val unit: String,
    /* url of an image of the product */
    val imageURL: String?,
    /* whether the product is vegan */
    val vegan: Boolean,
    /* amount of calories in the product */
    val energy: Int,
    /* amount of carbohydrates in 100g of the product */
    val carbs: Float,
    /* amount of protein in 100g of the product */
    val protein: Float,
    /* amount of fat in 100g of the product */
    val fat: Float
)
