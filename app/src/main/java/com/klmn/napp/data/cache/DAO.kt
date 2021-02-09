package com.klmn.napp.data.cache

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.klmn.napp.data.cache.entities.CacheEntities
import com.klmn.napp.model.Filter
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

    @JvmName("storeLabeledProducts")
    suspend fun storeProducts(products: List<CacheEntities.LabeledProduct>) {
        storeProducts(products.map { it.product })
        storeLabels(products.flatMap { it.labels })
    }

    @Transaction
    @RawQuery
    protected abstract suspend fun getProducts(query: SupportSQLiteQuery): List<CacheEntities.LabeledProduct>

    /* implementation- when there's network: pass products directly from network to ui &
    * store em in background, only bother displaying cached items when there's no network?
    * other approach- always show matching cached items first, then start pagination */
    suspend fun getProducts(
        query: String,
        filters: Iterable<Filter>?,
        order: String // todo
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