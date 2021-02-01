package com.klmn.napp.data.network.entities

import com.klmn.napp.model.Product
import com.klmn.slapp.common.EntityModelMapper

object ProductNetworkEntityMapper : EntityModelMapper<ProductEntity, Product> {
    override fun toEntity(model: Product): ProductEntity {
        throw IllegalStateException("should not convert product models to network entities!")
    }

    override fun toModel(entity: ProductEntity) = Product(
            entity.product_name!!,
            entity.quantity!!.filter { it.isDigit() }.toInt(),
            entity.image_small_url,
            NutrimentsNetworkEntityMapper.toModel(entity.nutriments)
    )
}