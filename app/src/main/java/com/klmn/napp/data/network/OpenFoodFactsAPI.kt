package com.klmn.napp.data.network

import com.klmn.napp.model.Search
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodFactsAPI {
    @GET("/cgi/search.pl?json=1")
    suspend fun getProducts(
        @Query("search_terms") query: String,
        @Query("fields") fields: String
    ): Response<Search>

    @GET("/categories?json=1")
    suspend fun getCategories(): Response<Search>
}