package com.klmn.napp.data.cache

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.klmn.napp.data.cache.entities.CacheEntities
import com.klmn.napp.model.Filter
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

@Dao
abstract class DAO {
    /* category operations */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun storeCategories(categories: List<CacheEntities.Category>)

    @Query("SELECT * FROM categories")
    abstract suspend fun getCategories(): List<CacheEntities.Category>

    /* product operations */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun storeProducts(products: List<CacheEntities.Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun storeLabels(labels: List<CacheEntities.Label>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun storeNutrients(labels: List<CacheEntities.Nutrient>)

    @Transaction
    @Query("SELECT * FROM products WHERE id = :id")
    abstract suspend fun getProduct(id: Long): CacheEntities.LabeledProduct

    @Transaction
    @Query("SELECT * FROM products WHERE favorite = 1")
    abstract fun getFavorites(): Flow<List<CacheEntities.LabeledProduct>>

    @Query("UPDATE products SET favorite = :favorite WHERE id = :id")
    abstract suspend fun favoriteProduct(id: Long, favorite: Boolean)

    @JvmName("storeLabeledProducts")
    suspend fun storeProducts(products: List<CacheEntities.LabeledProduct>) {
        storeProducts(products.map { it.product })
        storeLabels(products.flatMap { it.labels })
        storeNutrients(products.flatMap { it.nutrients })
    }

    @Transaction
    @RawQuery
    protected abstract suspend fun getProducts(query: SupportSQLiteQuery): List<CacheEntities.LabeledProduct>

    suspend fun getProducts(
        query: String,
        filters: Iterable<Filter>?
    ): List<CacheEntities.LabeledProduct> {
        val criteria = filters?.map(::filterSelection).takeUnless {
            it.isNullOrEmpty()
        }?.joinToString("") ?: ""
        @Language("RoomSql")
        val selection = "SELECT * FROM products WHERE $criteria name LIKE '%$query%'"
        return getProducts(SimpleSQLiteQuery(selection))
    }

    @Language("RoomSql")
    private fun filterSelection(filter: Filter) = """id IN (SELECT product_id FROM labels WHERE (
        criterion = '${filter.criterion}' AND value LIKE '%${filter.value}%'
    )) AND """
}