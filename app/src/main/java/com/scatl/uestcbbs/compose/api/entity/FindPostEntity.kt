package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FindPostEntity(
    @Json(name = "position")
    val position: Int? = -1,
    @Json(name = "thread_id")
    val threadId: Int? = -1,
)
