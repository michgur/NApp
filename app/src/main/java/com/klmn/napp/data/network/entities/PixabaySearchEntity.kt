package com.klmn.napp.data.network.entities

data class PixabaySearchEntity(
    val hits: List<PixabayImageEntity>
)

data class PixabayImageEntity(
    val webformatURL: String
)