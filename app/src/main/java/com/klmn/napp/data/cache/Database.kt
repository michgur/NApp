package com.klmn.napp.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmn.napp.data.cache.entities.CacheEntities

@Database(
    version = 1,
    entities = [
        CacheEntities.Category::class,
        CacheEntities.Product::class,
        CacheEntities.Label::class,
        CacheEntities.Nutrient::class
    ]
)
abstract class Database : RoomDatabase() {
    abstract fun dao(): DAO
}