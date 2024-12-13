package com.scatl.uestcbbs.compose.api.entity.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/8/30 12:31:06
 */
@JsonClass(generateAdapter = true)
data class AddFriendEntity(
    @Json(name = "friend_status")
    val friendStatus: String? = "",
)