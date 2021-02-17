package com.klmn.napp.data.cache.entities

import androidx.room.*

object CacheEntities {
    @Entity(tableName = "products")
    data class Product(
        @PrimaryKey val id: Long,
        val name: String,
        val quantity: Float,
        val unit: String,
        val imageURL: String?,
        val vegan: Boolean,
        val energy: Int,
        val favorite: Boolean
    )

    @Entity(tableName = "labels")
    data class Label(
        @PrimaryKey(autoGenerate = true) val index: Int,
        val product_id: Long,
        val criterion: String,
        val value: String
    )

    @Entity(tableName = "nutrients")
    data class Nutrient(
        @PrimaryKey(autoGenerate = true) val index: Int,
        val product_id: Long,
        val nutrient: String,
        val quantity: Float
    )

    @Entity(tableName = "categories")
    data class Category(
        @PrimaryKey val id: String,
        val name: String,
        val image_url: String
    )

    data class LabeledProduct(
        @Embedded val product: Product,
        @Relation(parentColumn = "id", entityColumn = "product_id")
        val labels: List<Label>,
        @Relation(parentColumn = "id", entityColumn = "product_id")
        val nutrients: List<Nutrient>
    )
}