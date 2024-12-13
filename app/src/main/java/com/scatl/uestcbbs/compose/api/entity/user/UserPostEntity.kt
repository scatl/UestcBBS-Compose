package com.scatl.uestcbbs.compose.api.entity.user

import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserPostEntity(
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "page_size")
    val pageSize: Int? = 0,
    @Json(name = "recent_visitors")
    val recentVisitors: List<RecentVisitor?>? = listOf(),
    @Json(name = "rows")
    val rows: List<CommonThreadEntity>? = listOf(),
    @Json(name = "total")
    val total: Int? = 0,
    @Json(name = "user_summary")
    val userSummary: UserSummary? = UserSummary()
) {
    @JsonClass(generateAdapter = true)
    data class RecentVisitor(
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "uid")
        val uid: Int? = 0,
        @Json(name = "username")
        val username: String? = ""
    )

    @JsonClass(generateAdapter = true)
    data class UserSummary(
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
        @Json(name = "level_id")
        val levelId: Int? = 0,
        @Json(name = "medals")
        val medals: List<Int?>? = listOf(),
        @Json(name = "replies")
        val replies: Int? = 0,
        @Json(name = "threads")
        val threads: Int? = 0,
        @Json(name = "uid")
        val uid: Int? = 0,
        @Json(name = "username")
        val username: String? = "",
        @Json(name = "views")
        val views: Int? = 0
    ) {
        @JsonClass(generateAdapter = true)
        data class ExtCredits(
            @Json(name = "威望")
            val 威望: Int? = 0,
            @Json(name = "水滴")
            val 水滴: Int? = 0
        )
    }
}
