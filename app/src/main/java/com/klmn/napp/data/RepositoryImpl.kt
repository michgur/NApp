package com.klmn.napp.data

import android.content.Context
import com.klmn.napp.R
import com.klmn.napp.data.cache.entities.CategoryCacheMapper
import com.klmn.napp.data.cache.DAO
import com.klmn.napp.data.cache.entities.ProductCacheMapper
import com.klmn.napp.data.network.OpenFoodFactsAPI
import com.klmn.napp.data.network.PixabayAPI
import com.klmn.napp.data.network.entities.ProductNetworkMapper
import com.klmn.napp.model.Category
import com.klmn.napp.model.Filter
import com.klmn.napp.model.Product

class RepositoryImpl(
    private val context: Context,
    private val dao: DAO,
    private val openFoodFactsAPI: OpenFoodFactsAPI,
    private val pixabayAPI: PixabayAPI
) : Repository {
    override suspend fun getProducts(
        query: String,
        page: Int,
        pageSize: Int,
        filters: Iterable<Filter>?
    ) = if (page <= 0) dao.getProducts(query, filters, "").let { products ->
            ProductCacheMapper.toModelList(products)
        } else openFoodFactsAPI.getProducts(
            query,
            page,
            pageSize,
            OpenFoodFactsAPI.searchOptions(
                OpenFoodFactsAPI.OrderBy.DATE_MODIFIED,
                filters
            )
        ).let { response ->
            if (response.isSuccessful) response.body()?.products?.mapNotNull {
                ProductNetworkMapper.toModel(it)
            } ?: listOf()
            else throw RuntimeException(response.errorBody()?.string()) // blocking but who cares
        }.also { products ->
            dao.storeProducts(ProductCacheMapper.toEntityList(products))
        }

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