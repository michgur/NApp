package com.klmn.napp.data

import com.klmn.napp.model.Category
import com.klmn.napp.model.Filter
import com.klmn.napp.model.Product

interface Repository {
    suspend fun getProducts(
        query: String = "",
        page: Int = 0,  // use page=0 for cache, page>0 for network api call
        pageSize: Int = 20,
        filters: Iterable<Filter>? = null
    ): List<Product>

    suspend fun getCategories(): List<Category>

    suspend fun findProductById(id: Long): Product
}