package com.klmn.napp.data

import android.content.Context
import com.klmn.napp.R
import com.klmn.napp.data.cache.CategoryCacheMapper
import com.klmn.napp.data.cache.DAO
import com.klmn.napp.data.network.OpenFoodFactsAPI
import com.klmn.napp.data.network.PixabayAPI
import com.klmn.napp.data.network.entities.ProductEntity
import com.klmn.napp.model.Category
import com.klmn.napp.model.Product
import com.klmn.napp.util.EntityModelMapper
import kotlin.reflect.full.declaredMemberProperties

class RepositoryImpl(
    private val context: Context,
    private val dao: DAO,
    private val openFoodFactsAPI: OpenFoodFactsAPI,
    private val mapper: EntityModelMapper<ProductEntity, Product>,
    private val pixabayAPI: PixabayAPI
) : Repository {
    private val fieldsQuery = ProductEntity::class.declaredMemberProperties
        .map { it.name }
        .reduce { a, b -> "$a,$b" }

    override suspend fun getProducts(query: String, page: Int, pageSize: Int) = openFoodFactsAPI
        .getProducts(query, fieldsQuery, page, pageSize).let { response ->
            if (response.isSuccessful) response.body()?.products?.filterNot {
                it.product_name.isNullOrBlank() ||
                    it.quantity.isNullOrBlank()
            }?.let { mapper.toModelList(it) } ?: listOf()
            else throw RuntimeException(response.errorBody().toString())
        }

    override suspend fun getCategories() = dao.getCategories().let { cachedCategories ->
        if (cachedCategories.isEmpty()) {
            context.resources.getStringArray(R.array.categories).map { c ->
                c.split("|").let {
                    Category(it[0], it[1], getCategoryImageURL(it[1]))
                }
            }.also { dao.addCategories(CategoryCacheMapper.toEntityList(it)) }
        } else CategoryCacheMapper.toModelList(cachedCategories)
    }

    private suspend fun getCategoryImageURL(name: String) = pixabayAPI
        .getImageURL(encodeURL(name)).let { response ->
            if (response.isSuccessful)
                response.body()?.hits?.get(0)?.webformatURL
                    ?: response.body()?.hits?.get(1)?.webformatURL
                    ?: response.body()?.hits?.get(2)?.webformatURL ?: ""
            else throw RuntimeException(response.errorBody().toString())
        }

    private fun encodeURL(string: String) = string.replace(' ', '+')
}