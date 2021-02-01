package com.klmn.napp.data

import com.klmn.napp.model.Product

interface Repository {
    suspend fun getProducts(query: String = ""): List<Product>
}