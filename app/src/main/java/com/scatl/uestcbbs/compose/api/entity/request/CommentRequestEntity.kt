package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/10/22 11:07:13
 */
@JsonClass(generateAdapter = true)
data class CommentRequestEntity(
    @Json(name = "thread_id")
    var threadId: Int,
    @Json(name = "post_id")
    var postId: Int,
    @Json(name = "message")
    var message: String,
)