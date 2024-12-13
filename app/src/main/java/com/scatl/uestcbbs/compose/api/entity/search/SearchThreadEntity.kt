package com.scatl.uestcbbs.compose.api.entity.search

import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchThreadEntity(
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "page_size")
    val pageSize: Int? = 0,
    @Json(name = "rows")
    val rows: List<CommonThreadEntity>? = listOf(),
    @Json(name = "total")
    val total: Int? = 0
)