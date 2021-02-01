package com.klmn.napp.model

import com.klmn.napp.data.network.entities.ProductEntity

data class Search(
    val page_size: Int = 0,
    val page: Int = 0,
    val page_count: Int = 0,
    val count: Int = 0,
    val skip: Int = 0,
    val products: List<ProductEntity> = listOf()
)
