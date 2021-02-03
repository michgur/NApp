package com.klmn.napp.data

import com.klmn.napp.model.Category
import com.klmn.napp.model.Product

interface Repository {
    suspend fun getProducts(query: String = "", page: Int = 1): List<Product>

    suspend fun getCategories(): List<Category>
}