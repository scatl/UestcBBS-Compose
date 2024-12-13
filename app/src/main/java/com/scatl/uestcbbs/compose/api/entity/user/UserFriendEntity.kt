package com.scatl.uestcbbs.compose.api.entity.user

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFriendEntity(
    @Json(name = "hidden")
    val hidden: Boolean? = false,
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
        @Json(name = "credits")
        val credits: Int? = 0,
        @Json(name = "digests")
        val digests: Int? = 0,
        @Json(name = "ext_credits")
        val extCredits: ExtCredits? = ExtCredits(),
        @Json(name = "friends")
        val friends: Int? = 0,
        @Json(name = "group_icon")
        val groupIcon: String? = "",
        @Json(name = "group_id")
        val groupId: Int? = 0,
        @Json(name = "group_subtitle")
        val groupSubtitle: String? = "",
        @Json(name = "group_title")
        val groupTitle: String? = "",
        @Json(name = "latest_thread")
        val latestThread: LatestThread? = LatestThread(),
        @Json(name = "level_id")
        val levelId: Int? = 0,
        @Json(name = "replies")
        val replies: Int? = 0,
        @Json(name = "threads")
        val threads: Int? = 0,
        @Json(name = "uid")
        val uid: Int? = 0,
        @Json(name = "username")
        val username: String? = "",
        @Json(name = "note")
        val note: String? = "",

        override var isStickerHeader: Boolean = false
    ): SwipeRefreshItem {
        @JsonClass(generateAdapter = true)
        data class ExtCredits(
            @Json(name = "威望")
            val weiWang: Int? = 0,
            @Json(name = "水滴")
            val water: Int? = 0
        )

        @JsonClass(generateAdapter = true)
        data class LatestThread(
            @Json(name = "dateline")
            val dateline: Int? = 0,
            @Json(name = "subject")
            val subject: String? = "",
            @Json(name = "tid")
            val tid: Int? = 0
        )
    }
}