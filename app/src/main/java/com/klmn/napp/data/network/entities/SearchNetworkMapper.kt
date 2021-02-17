package com.klmn.napp.data.network.entities

import com.klmn.napp.model.Search
import com.klmn.napp.util.EntityModelMapper

object SearchNetworkMapper : EntityModelMapper<NetworkEntities.Search?, Search> {
    override fun toEntity(model: Search): NetworkEntities.Search? {
        throw IllegalStateException("should not convert search models to network entities!")
    }

    override fun toModel(entity: NetworkEntities.Search?) = entity?.let {
        Search(
            entity.page,
            entity.page_count < entity.page_size,
            false,
            entity.page_count,
            entity.products.mapNotNull(ProductNetworkMapper::toModel)
        )
    } ?: Search(0, lastPage = false, cached = true, products = listOf(), originalCount = 0)
}