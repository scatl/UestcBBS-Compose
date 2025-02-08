package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteFavoriteRequestEntity(
    @Json(name = "personal_favorite")
    var personalFavorite: Boolean,
    @Json(name = "tid_list")
    var tidList: List<Int>,
    @Json(name = "collection_id")
    var collectionId: Int? = 0,
    @Json(name = "favorite_id_list")
    var favoriteIdList: List<Int> = mutableListOf(),
)
