package com.klmn.napp.data

import com.klmn.napp.data.network.OFFAPI
import com.klmn.napp.data.network.entities.ProductEntity
import com.klmn.napp.model.Product
import com.klmn.slapp.common.EntityModelMapper
import kotlin.reflect.full.declaredMemberProperties

class RepositoryImpl(
    private val api: OFFAPI,
    private val mapper: EntityModelMapper<ProductEntity, Product>
) : Repository {
    private val fieldsQuery = ProductEntity::class.declaredMemberProperties
        .map { it.name }
        .reduce { a, b -> "$a,$b" }

    override suspend fun getProducts() = api.getProducts(fieldsQuery).let { response ->
        if (response.isSuccessful) response.body()?.products?.filterNot {
            it.product_name.isNullOrBlank() ||
            it.quantity.isNullOrBlank()
        }?.let { mapper.toModelList(it) } ?: listOf()
        else throw RuntimeException(response.errorBody().toString())
    }
}