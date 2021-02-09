package com.klmn.napp.data.cache.entities

import com.klmn.napp.model.Product
import com.klmn.napp.util.EntityModelMapper

object ProductCacheMapper : EntityModelMapper<CacheEntities.LabeledProduct, Product> {
    override fun toEntity(model: Product) = CacheEntities.LabeledProduct(
        CacheEntities.Product(
            model.id,
            model.name,
            model.quantity,
            model.unit,
            model.imageURL,
            model.vegan,
            model.energy,
            model.carbs,
            model.protein,
            model.fat
        ),
        model.labels.map { label ->
            CacheEntities.Label(
                0,
                model.id,
                label.key,
                label.value.joinToString(", ")
            )
        }
    )

    override fun toModel(entity: CacheEntities.LabeledProduct) = Product(
        entity.product.id,
        entity.product.name,
        entity.product.quantity,
        entity.product.unit,
        entity.product.imageURL,
        entity.product.vegan,
        entity.product.energy,
        entity.product.carbs,
        entity.product.protein,
        entity.product.fat,
        entity.labels.associate { label ->
            label.criterion to label.value.split(", ")
        }
    )
}