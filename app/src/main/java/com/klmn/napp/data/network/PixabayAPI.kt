package com.klmn.napp.data.network

import com.klmn.napp.data.network.entities.NetworkEntities
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayAPI {
    @GET("?image_type=photo&per_page=3")
    suspend fun getImageURL(
        @Query("q") query: String
    ): Response<NetworkEntities.PixabaySearch>
}