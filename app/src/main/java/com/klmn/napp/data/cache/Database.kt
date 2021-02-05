package com.klmn.napp.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmn.napp.data.cache.entities.CacheEntities

@Database(entities = [CacheEntities.Category::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun dao(): DAO
}