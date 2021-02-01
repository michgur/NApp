package com.klmn.napp.data.network.entities

import com.klmn.napp.model.Nutriments
import com.klmn.slapp.common.EntityModelMapper

object NutrimentsNetworkEntityMapper : EntityModelMapper<NutrimentsEntity, Nutriments> {
    override fun toEntity(model: Nutriments): NutrimentsEntity {
        throw IllegalStateException("should not convert product models to network entities!")
    }

    override fun toModel(entity: NutrimentsEntity) = Nutriments(
            entity.carbohydrates_100g,
            entity.carbohydrates_unit,
            entity.energy,
            entity.energy_unit,
            entity.fat_100g,
            entity.fat_unit,
            entity.proteins_100g,
            entity.proteins_unit
    )
}