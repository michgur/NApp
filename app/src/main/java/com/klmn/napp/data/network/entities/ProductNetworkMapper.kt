package com.klmn.napp.data.network.entities

import com.klmn.napp.model.Product
import com.klmn.napp.util.EntityModelMapper
import com.klmn.napp.util.extractQuantityAndUnit
import com.klmn.napp.util.fixQuantity

/* maps VALID product network entities to models, invalid entities to null */
object ProductNetworkMapper : EntityModelMapper<NetworkEntities.Product, Product?> {
    override fun toEntity(model: Product?): NetworkEntities.Product {
        throw IllegalStateException("should not convert product models to network entities!")
    }

    override fun toModel(entity: NetworkEntities.Product): Product? {
        val (quantity, unit) = extractQuantityAndUnit(entity.quantity ?: return null)
            ?: return null
        return Product(
            entity.product_name ?: return null,
            quantity?.toInt() ?: return null,
            unit,
            entity.image_small_url,
            entity.ingredients_analysis_tags?.contains("en:vegan") ?: false,
            fixQuantity(
                (entity.nutriments ?: return null).energy,
                entity.nutriments.energy_unit
            )?.first?.toInt() ?: return null,
            fixQuantity(
                entity.nutriments.carbohydrates_100g,
                entity.nutriments.carbohydrates_unit
            )?.first ?: return null,
            fixQuantity(
                entity.nutriments.proteins_100g,
                entity.nutriments.proteins_unit
            )?.first ?: return null,
            fixQuantity(
                entity.nutriments.fat_100g,
                entity.nutriments.fat_unit
            )?.first ?: return null
        )
    }
}