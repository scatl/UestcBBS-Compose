package com.scatl.uestcbbs.compose.api.entity.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/8/20 14:07:57
 */
@JsonClass(generateAdapter = true)
data class NewNotificationEntity(
    @Json(name = "author")
    val author: String? = "",
    @Json(name = "author_id")
    val authorId: Int? = 0,
    @Json(name = "category")
    val category: Int? = 0,
    @Json(name = "dateline")
    val dateline: Int? = 0,
    @Json(name = "from_id")
    val fromId: Int? = 0,
    @Json(name = "from_id_type")
    val fromIdType: String? = "",
    @Json(name = "from_num")
    val fromNum: Int? = 0,
    @Json(name = "html_message")
    val htmlMessage: String? = "",
    @Json(name = "id")
    val id: Int? = 0,
    @Json(name = "type")
    val type: String? = "",
    @Json(name = "unread")
    val unread: Boolean? = false,
    @Json(name = "user_id")
    val userId: Int? = 0
)