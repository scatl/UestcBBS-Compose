package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/9/5 13:44:50
 */
@JsonClass(generateAdapter = true)
data class UserEditCommentRequestEntity(
    @Json(name = "message")
    var message: String
)