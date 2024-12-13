package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/8/30 9:14:27
 */
@JsonClass(generateAdapter = true)
data class AddFriendRequestEntity(
    @Json(name = "uid")
    var uid: Int,
    @Json(name = "message")
    var message: String
)