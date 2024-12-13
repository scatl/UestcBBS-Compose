package com.scatl.uestcbbs.compose.api.entity.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddCommentEntity(
    @Json(name = "comment_id")
    val commentId: Int? = 0
)