package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoteRequestEntity(
    @Json(name = "thread_id")
    var threadId: Int,
    @Json(name = "options")
    var options: List<Int>,
)
