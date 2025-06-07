package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IndexEntity(
    @Json(name = "announcement")
    val announcement: List<Announcement> = listOf(),
    @Json(name = "forum_list")
    val forumList: List<Forum> = listOf(),
    @Json(name = "global_stat")
    val globalStat: GlobalStat? = GlobalStat(),
) {
    @JsonClass(generateAdapter = true)
    data class Announcement(
        @Json(name = "dark_highlight_color")
        val darkHighlightColor: String? = "",
        @Json(name = "highlight_color")
        val highlightColor: String? = "",
        @Json(name = "href")
        val href: String? = "",
        @Json(name = "kind")
        val kind: Int? = 0,
        @Json(name = "summary")
        val summary: String? = "",
        @Json(name = "title")
        val title: String? = ""
    )

    @JsonClass(generateAdapter = true)
    data class Forum(
        @Json(name = "can_post_reply")
        val canPostReply: Boolean? = false,
        @Json(name = "can_post_thread")
        val canPostThread: Boolean? = false,
        @Json(name = "children")
        var children: List<Forum>? = listOf(),
        @Json(name = "fid")
        val fid: Int? = 0,
        @Json(name = "hidden_home")
        val hiddenHome: Boolean? = false,
        @Json(name = "latest_thread")
        val latestThread: LatestThread? = null,
        @Json(name = "moderators")
        val moderators: List<String>? = listOf(),
        @Json(name = "name")
        val name: String? = "",
        @Json(name = "posts")
        val posts: Int? = 0,
        @Json(name = "threads")
        val threads: Int? = 0,
        @Json(name = "todayposts")
        val todayPosts: Int? = 0,
        @Json(name = "yesterdayposts")
        val yesterdayPosts: Int? = 0,

        var parent: Forum? = null
    ) {
        @JsonClass(generateAdapter = true)
        data class LatestThread(
            @Json(name = "lastpost_author")
            val lastPostAuthor: String? = "",
            @Json(name = "lastpost_authorid")
            val lastPostAuthorid: Int? = 0,
            @Json(name = "lastpost_time")
            val lastPostTime: Int? = 0,
            @Json(name = "subject")
            val subject: String? = "",
            @Json(name = "thread_id")
            val threadId: Int? = 0
        )
    }

    @JsonClass(generateAdapter = true)
    data class GlobalStat(
        @Json(name = "new_user")
        val newUser: NewUser? = NewUser(),
        @Json(name = "today_posts")
        val todayPosts: Int? = 0,
        @Json(name = "total_posts")
        val totalPosts: Int? = 0,
        @Json(name = "total_users")
        val totalUsers: Int? = 0,
        @Json(name = "yesterday_posts")
        val yesterdayPosts: Int? = 0
    ) {
        @JsonClass(generateAdapter = true)
        data class NewUser(
            @Json(name = "uid")
            val uid: Int? = 0,
            @Json(name = "username")
            val username: String? = ""
        )
    }
}
