package com.scatl.uestcbbs.compose.api.entity.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileEntity(
    @Json(name = "custom_title")
    val customTitle: String? = "",
    @Json(name = "introduction")
    val introduction: String? = "",
    @Json(name = "last_activity")
    val lastActivity: Int? = 0,
    @Json(name = "last_post")
    val lastPost: Int? = 0,
    @Json(name = "last_visit")
    val lastVisit: Int? = 0,
    @Json(name = "online_time")
    val onlineTime: Int? = 0,
    @Json(name = "recent_visitors")
    val recentVisitors: List<RecentVisitor>? = listOf(),
    @Json(name = "register_time")
    val registerTime: Int? = 0,
    @Json(name = "signature")
    val signature: String? = "",
    @Json(name = "signature_format")
    val signatureFormat: String? = "",
    @Json(name = "email")
    val email: String? = "",
    @Json(name = "register_ip")
    val registerIp: String? = "",
    @Json(name = "last_ip")
    val lastIp: String? = "",
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
        @Json(name = "friends_hidden")
        val friendsHidden: Boolean? = false,
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
        val medals: List<Int>? = listOf(),
        @Json(name = "replies")
        val replies: Int? = 0,
        @Json(name = "threads")
        val threads: Int? = 0,
        @Json(name = "uid")
        val uid: Int? = 0,
        @Json(name = "username")
        val username: String? = "",
        @Json(name = "views")
        val views: Int? = 0,
        @Json(name = "friend_status")
        val friendStatus: String? = "", // 'requested' 已发送请求，等待通过；'friend' 好友；
        @Json(name = "blocked")
        val blocked: Boolean? = false,
        @Json(name = "favorites_unavailable")
        val favoritesUnavailable: Boolean? = false, //收藏是否隐藏
        @Json(name = "comments_hidden")
        val commentsHidden: Boolean? = false, //留言是否隐藏
    ) {
        @JsonClass(generateAdapter = true)
        data class ExtCredits(
            @Json(name = "威望")
            val weiWang: Int? = 0,
            @Json(name = "水滴")
            val water: Int? = 0
        )
    }
}