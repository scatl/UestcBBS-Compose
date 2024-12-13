package com.scatl.uestcbbs.compose.api.entity.search

import androidx.compose.ui.util.fastCbrt
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchSummaryEntity(
    @Json(name = "thread_count")
    val threadCount: Int? = 0,
    @Json(name = "threads")
    var threads: MutableList<Thread>? = mutableListOf(),
    @Json(name = "tid_match")
    val tidMatch: Thread? = null,
    @Json(name = "uid_match")
    val uidMatch: User? = null,
    @Json(name = "user_count")
    val userCount: Int? = 0,
    @Json(name = "users")
    var users: MutableList<User>? = mutableListOf()
) {
    @JsonClass(generateAdapter = true)
    data class Thread(
        @Json(name = "author")
        val author: String? = "",
        @Json(name = "author_id")
        val authorId: Int? = 0,
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "forum_id")
        val forumId: Int? = 0,
        @Json(name = "subject")
        val subject: String? = "",
        @Json(name = "thread_id")
        val threadId: Int? = 0,

        var tidMatch: Boolean = false
    )

    @JsonClass(generateAdapter = true)
    data class User(
        @Json(name = "group_icon")
        val groupIcon: String? = "",
        @Json(name = "group_id")
        val groupId: Int? = 0,
        @Json(name = "group_subtitle")
        val groupSubtitle: String? = "",
        @Json(name = "group_title")
        val groupTitle: String? = "",
        @Json(name = "level_id")
        val levelId: Int? = 0,
        @Json(name = "uid")
        val uid: Int? = 0,
        @Json(name = "username")
        val username: String? = "",

        var uidMatch: Boolean = false
    ): SwipeRefreshItem {
        override var isStickerHeader: Boolean = false
    }
}