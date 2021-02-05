package com.klmn.napp.data.network

import com.klmn.napp.data.network.entities.NetworkEntities
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodFactsAPI {
    @GET("/cgi/search.pl?json=1")
    suspend fun getProducts(
        @Query("search_terms") query: String,
        @Query("fields") fields: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Response<NetworkEntities.Search>

    @GET("/categories?json=1")
    suspend fun getCategories(
        @Query("string") query: String
    ): Response<NetworkEntities.Search>
}