package com.klmn.napp.data

import android.content.Context
import android.util.Log
import com.klmn.napp.R
import com.klmn.napp.data.cache.entities.CategoryCacheMapper
import com.klmn.napp.data.cache.DAO
import com.klmn.napp.data.cache.entities.ProductCacheMapper
import com.klmn.napp.data.network.OpenFoodFactsAPI
import com.klmn.napp.data.network.PixabayAPI
import com.klmn.napp.data.network.entities.SearchNetworkMapper
import com.klmn.napp.model.Category
import com.klmn.napp.model.Filter
import com.klmn.napp.model.Search

class RepositoryImpl(
    private val context: Context,
    private val dao: DAO,
    private val openFoodFactsAPI: OpenFoodFactsAPI,
    private val pixabayAPI: PixabayAPI
) : Repository {
    companion object { const val TAG = "RepositoryImpl" }

    override suspend fun searchProducts(
        query: String,
        page: Int,
        pageSize: Int,
        filters: Iterable<Filter>?
    ) = if (page <= 0) dao.getProducts(query, filters).let { products ->
            Log.d(TAG, "retrieved cached products (query=$query, filters=$filters)")
            Search(
                0,
                lastPage = false,
                cached = true,
                originalCount = 0,
                products = ProductCacheMapper.toModelList(products)
            )
        } else openFoodFactsAPI.getProducts(
            query,
            page,
            pageSize,
            OpenFoodFactsAPI.searchOptions(
                OpenFoodFactsAPI.OrderBy.DATE_MODIFIED,
                filters
            )
        ).let { response ->
            Log.d(TAG, "retrieved network api products (query=$query, page=$page, filters=$filters), success=${response.isSuccessful}")
            if (response.isSuccessful) SearchNetworkMapper.toModel(response.body())
            else throw RuntimeException(response.errorBody()?.string()) // blocking but who cares
        }.also { search ->
            Log.d(TAG, "caching network products")
            dao.storeProducts(ProductCacheMapper.toEntityList(search.products))
        }

    override suspend fun getFavoriteProducts() =
        dao.getFavorites().let(ProductCacheMapper::toModelListFlow)

    override suspend fun findProductById(id: Long) =
        dao.getProduct(id).let(ProductCacheMapper::toModel)

    override suspend fun favoriteProduct(id: Long, favorite: Boolean) =
        dao.favoriteProduct(id, favorite)

    override suspend fun getCategories() = dao.getCategories().let { cachedCategories ->
        if (cachedCategories.isEmpty()) {
            context.resources.getStringArray(R.array.categories).map { c ->
                c.split("|").let {
                    Category(it[0], it[1], getCategoryImageURL(it[1]))
                }
            }.also { dao.storeCategories(CategoryCacheMapper.toEntityList(it)) }
        } else CategoryCacheMapper.toModelList(cachedCategories)
    }

    private suspend fun getCategoryImageURL(name: String) = pixabayAPI
        .getImageURL(encodeURL(name)).let { response ->
            if (response.isSuccessful)
                response.body()?.hits?.get(0)?.webformatURL ?:
                response.body()?.hits?.get(1)?.webformatURL ?:
                response.body()?.hits?.get(2)?.webformatURL ?: ""
            else throw RuntimeException(response.errorBody().toString())
        }

    private fun encodeURL(string: String) = string.replace(' ', '+')
}