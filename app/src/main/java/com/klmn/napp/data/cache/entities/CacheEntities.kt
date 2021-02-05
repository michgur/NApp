package com.klmn.napp.data.cache.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

object CacheEntities {
    @Entity(tableName = "categories")
    data class Category(
        @PrimaryKey val id: String,
        val name: String,
        val image_url: String
    )
}