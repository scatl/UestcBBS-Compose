package com.scatl.uestcbbs.compose.api.entity.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/8/20 14:08:30
 */
@JsonClass(generateAdapter = true)
data class NewChatEntity(
    @Json(name = "author_id")
    val authorId: Int? = 0,
    @Json(name = "conversation_id")
    val conversationId: Int? = 0,
    @Json(name = "create_time")
    val createTime: Int? = 0,
    @Json(name = "last_author")
    val lastAuthor: String? = "",
    @Json(name = "last_author_id")
    val lastAuthorId: Int? = 0,
    @Json(name = "last_dateline")
    val lastDateline: Int? = 0,
    @Json(name = "last_summary")
    val lastSummary: String? = "",
    @Json(name = "last_update")
    val lastUpdate: Int? = 0,
    @Json(name = "member_count")
    val memberCount: Int? = 0,
    @Json(name = "message_count")
    val messageCount: Int? = 0,
    @Json(name = "subject")
    val subject: String? = "",
    @Json(name = "to_uid")
    val toUid: Int? = 0,
    @Json(name = "to_username")
    val toUsername: String? = "",
    @Json(name = "type")
    val type: String? = "",
    @Json(name = "unread")
    val unread: Boolean? = false
)