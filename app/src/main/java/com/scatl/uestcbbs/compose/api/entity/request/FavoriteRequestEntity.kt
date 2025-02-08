package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteRequestEntity(
    @Json(name = "personal_favorite")
    var personalFavorite: Boolean,
    @Json(name = "collection_id")
    var collectionId: Int? = null
)