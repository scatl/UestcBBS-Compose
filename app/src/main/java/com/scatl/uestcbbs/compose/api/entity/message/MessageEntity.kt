package com.scatl.uestcbbs.compose.api.entity.message

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageEntity(
    @Json(name = "new_messages")
    val newMessages: NewMessageEntity? = NewMessageEntity(),
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "page_size")
    val pageSize: Int? = 0,
    @Json(name = "rows")
    val rows: List<Row>? = listOf(),
    @Json(name = "total")
    val total: Int? = 0
) {

    @JsonClass(generateAdapter = true)
    data class Row(
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
        @Json(name = "kind")
        val kind: String? = "",
        @Json(name = "post_id")
        val postId: Int? = 0,
        @Json(name = "subject")
        val subject: String? = "",
        @Json(name = "summary")
        val summary: String? = "",
        @Json(name = "thread_id")
        val threadId: Int? = 0,
        @Json(name = "type")
        val type: String? = "",
        @Json(name = "unread")
        val unread: Boolean? = false,
        @Json(name = "user_id")
        val userId: Int? = 0,
        @Json(name = "reason")
        val reason: String? = "",
        @Json(name = "friend_note")
        val friendNote: String? = "",
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
        @Json(name = "to_uid")
        val toUid: Int? = 0,
        @Json(name = "to_username")
        val toUsername: String? = "",
        @Json(name = "credits")
        val credits: Credits? = Credits(),
    ): SwipeRefreshItem {
        override var isStickerHeader = false

        @JsonClass(generateAdapter = true)
        data class Credits(
            @Json(name = "水滴")
            val water: Int? = null,
            @Json(name = "威望")
            val weiWang: Int? = null
        )
    }
}