package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAddCommentRequestEntity(
    @Json(name = "uid")
    var uid: Int,
    @Json(name = "message")
    var message: String
)
