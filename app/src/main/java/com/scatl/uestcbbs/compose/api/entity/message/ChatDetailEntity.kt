package com.scatl.uestcbbs.compose.api.entity.message

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatDetailEntity(
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
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "message")
        val message: String? = "",
        @Json(name = "message_id")
        val messageId: Int? = 0,

        override var isStickerHeader: Boolean = false
    ): SwipeRefreshItem
}