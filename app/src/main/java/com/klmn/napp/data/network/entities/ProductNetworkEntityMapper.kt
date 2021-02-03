package com.klmn.napp.data.network.entities

import com.klmn.napp.model.Product
import com.klmn.napp.util.EntityModelMapper

object ProductNetworkEntityMapper : EntityModelMapper<ProductEntity, Product> {
    override fun toEntity(model: Product): ProductEntity {
        throw IllegalStateException("should not convert product models to network entities!")
    }

    override fun toModel(entity: ProductEntity) = Product(
        entity.product_name!!,
        entity.quantity!!.filter { it.isDigit() }.toInt(),
        entity.quantity.filter { it.isLetter() },
        entity.image_small_url,
        entity.ingredients_analysis_tags?.contains("en:vegan") ?: false,
        NutrimentsNetworkEntityMapper.toModel(entity.nutriments)
    )
}