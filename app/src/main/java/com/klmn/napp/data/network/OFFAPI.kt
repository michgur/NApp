package com.klmn.napp.data.network

import com.klmn.napp.model.Search
import retrofit2.Response
import retrofit2.http.GET

interface OFFAPI {
    @GET("category/chocolate-spreads/1.json?fields=product_name,quantity,nutriments")
    suspend fun getProducts(): Response<Search>
}