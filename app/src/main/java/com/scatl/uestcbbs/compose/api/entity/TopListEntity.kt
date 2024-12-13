package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopListEntity(
    @Json(name = "newthread")
    val newThread: List<CommonThreadEntity>? = listOf(),
    @Json(name = "newreply")
    val newReply: List<CommonThreadEntity>? = listOf(),
    @Json(name = "life")
    val life: List<CommonThreadEntity>? = listOf(),
    @Json(name = "digest")
    val digest: List<CommonThreadEntity>? = listOf(),
    @Json(name = "hotlist")
    val hotList: List<CommonThreadEntity>? = listOf()
) {
}