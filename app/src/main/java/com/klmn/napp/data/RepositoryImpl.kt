package com.klmn.napp.data

import com.klmn.napp.data.network.OFFAPI

class RepositoryImpl(
    private val api: OFFAPI
) : Repository {

    override suspend fun getProducts() = api.getProducts().let { response ->
        if (response.isSuccessful) response.body()?.products!!
        else throw RuntimeException(response.errorBody().toString())
    }
}