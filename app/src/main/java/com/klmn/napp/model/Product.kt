package com.klmn.napp.model

data class Product(
    /* open-food-facts API id of the product */
    val id: Long,
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
    val fat: Float,
    /* additional product labels, i.e. countries, categories, etc. used for complex search */
    val labels: Map<String, List<String>>,
    /* nutrients listing per 100g of the product */
    val nutrients: Map<String, Float>,
    /* whether the product was starred by the user */
    val favorite: Boolean
)
