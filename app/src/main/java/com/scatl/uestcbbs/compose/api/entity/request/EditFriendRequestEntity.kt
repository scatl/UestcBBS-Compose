package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/9/4 16:36:56
 */
@JsonClass(generateAdapter = true)
data class EditFriendRequestEntity(
    @Json(name = "note")
    var note: String
)