package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForumThreadsEntity(
    @Json(name = "forum")
    val forum: ForumDetailEntity? = ForumDetailEntity(),
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "page_size")
    val pageSize: Int? = 0,
    @Json(name = "rows")
    val rows: MutableList<CommonThreadEntity>? = mutableListOf(),
    @Json(name = "total")
    val total: Int? = 0
)