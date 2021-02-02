package com.klmn.napp.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategories(categories: List<CacheEntities.Category>)

    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<CacheEntities.Category>
}