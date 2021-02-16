package com.klmn.napp.data.network.entities

import com.klmn.napp.model.Product
import com.klmn.napp.util.EntityModelMapper
import com.klmn.napp.util.extractQuantityAndUnit
import com.klmn.napp.util.fixQuantity
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

/* maps VALID product network entities to models, invalid entities to null */
object ProductNetworkMapper : EntityModelMapper<NetworkEntities.Product, Product?> {
    override fun toEntity(model: Product?): NetworkEntities.Product {
        throw IllegalStateException("should not convert product models to network entities!")
    }

    override fun toModel(entity: NetworkEntities.Product): Product? {
        val (quantity, unit) = extractQuantityAndUnit(entity.quantity ?: return null)
            ?: return null
        return Product(
            entity.id?.toLongOrNull() ?: return null,
            entity.product_name ?: return null,
            quantity ?: return null,
            unit,
            entity.image_url,
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
            )?.first ?: return null,
            mutableMapOf<String, List<String>>().also { initLabelsMap(entity, it) },
            initNutrientsMap(entity.nutriments)
        )
    }

    private val labelFields = NetworkEntities.Product::class.declaredMemberProperties.filter {
        it.hasAnnotation<ProductLabel>()
    }
    private fun initLabelsMap(
        product: NetworkEntities.Product,
        map: MutableMap<String, List<String>>
    ) = labelFields.forEach { label ->
        (label.get(product) as? String).takeUnless {
            it.isNullOrBlank()
        }?.let { values ->
            map[label.name] = values.split(",").mapNotNull {
                it.takeIf { it.isNotBlank() }?.trim()
            }
        }
    }

    private val nutrientFields = NetworkEntities.Nutriments::class.declaredMemberProperties.mapNotNull {
        it.takeUnless { it.name.endsWith("_unit") }
    }.associateWith {
        it.name.replace("100g", "")
            .replace('_', ' ')
            .toLowerCase(Locale.ROOT)
            .capitalize(Locale.ROOT)
            .trim()
    }
    private fun initNutrientsMap(nutriments: NetworkEntities.Nutriments) =
        mutableMapOf<String, Float>().also { map ->
            for ((property, name) in nutrientFields)
                (property.get(nutriments) as? String)?.toFloatOrNull()?.let {
                    map[name] = it
                }
        }
}