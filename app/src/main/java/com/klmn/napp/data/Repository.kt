package com.klmn.napp.data

import com.klmn.napp.model.Category
import com.klmn.napp.model.Filter
import com.klmn.napp.model.Product
import com.klmn.napp.model.Search

interface Repository {
    /* search for products matching query & filters, use page=0 for cached products
    * amount of products is not guaranteed to match pageSize as some products are ignored*/
    suspend fun searchProducts(
        query: String = "",
        page: Int = 0,  // use page=0 for cache, page>0 for network api call
        pageSize: Int = 20,
        filters: Iterable<Filter>? = null
    ): Search

    suspend fun getCategories(): List<Category>

    suspend fun findProductById(id: Long): Product
}