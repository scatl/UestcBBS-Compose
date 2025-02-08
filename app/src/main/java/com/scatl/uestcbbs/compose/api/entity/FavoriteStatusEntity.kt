package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteStatusEntity(
    @Json(name = "is_personal_favorite")
    var isPersonalFavorite: Boolean? = false,
    @Json(name = "public_favorites")
    var publicFavorites: List<CollectionEntity>? = listOf()
)