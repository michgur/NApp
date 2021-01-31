package com.klmn.napp.model

data class Search(
    val page_size: Int = 0,
    val page: String = "",
    val page_count: Int = 0,
    val count: String = "",
    val skip: Int = 0,
    val products: List<Product> = listOf()
)
