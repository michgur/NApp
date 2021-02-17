package com.klmn.napp.model

data class Search(
    val page: Int,
    val lastPage: Boolean,
    val cached: Boolean,
    val originalCount: Int,
    val products: List<Product> = listOf()
)
