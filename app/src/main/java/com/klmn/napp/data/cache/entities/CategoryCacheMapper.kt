package com.klmn.napp.data.cache.entities

import com.klmn.napp.model.Category
import com.klmn.napp.util.EntityModelMapper

object CategoryCacheMapper : EntityModelMapper<CacheEntities.Category, Category> {
    override fun toEntity(model: Category) = CacheEntities.Category(
        model.id,
        model.name,
        model.imageURL ?: ""
    )

    override fun toModel(entity: CacheEntities.Category) = Category(
        entity.id,
        entity.name,
        entity.image_url
    )
}