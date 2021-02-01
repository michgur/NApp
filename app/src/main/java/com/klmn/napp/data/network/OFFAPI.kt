package com.klmn.napp.data.network

import com.klmn.napp.model.Search
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OFFAPI {
    @GET("/1.json")
    suspend fun getProducts(
            @Query("fields") fields: String
    ): Response<Search>
}