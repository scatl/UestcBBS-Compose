package com.scatl.uestcbbs.compose.api.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ForumDetailEntity(
    @Json(name = "announcement")
    val announcement: String? = "",
    @Json(name = "announcement_format")
    val announcementFormat: String? = "",
    @Json(name = "can_post_anonymously")
    val canPostAnonymously: Boolean? = false,
    @Json(name = "can_post_reply")
    val canPostReply: Boolean? = false,
    @Json(name = "can_post_thread")
    val canPostThread: Boolean? = false,
    @Json(name = "children")
    var children: MutableList<ForumDetailEntity>? = mutableListOf(),
    @Json(name = "fid")
    val fid: Int? = 0,
    @Json(name = "latest_thread")
    val latestThread: LatestThread? = null,
    @Json(name = "moderators")
    val moderators: List<String?>? = listOf(),
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "optional_thread_type")
    val optionalThreadType: Boolean? = false,
    @Json(name = "parents")
    val parents: List<ForumDetailEntity>? = listOf(),
    @Json(name = "post_notice")
    val postNotice: PostNotice? = PostNotice(),
    @Json(name = "post_notice_format")
    val postNoticeFormat: String? = "",
    @Json(name = "posts")
    val posts: Int? = 0,
    @Json(name = "reply_credit")
    val replyCredit: ReplyCredit? = ReplyCredit(),
    @Json(name = "thread_types")
    val threadTypes: List<ThreadType>? = listOf(),
    @Json(name = "threads")
    val threads: Int? = 0,
    @Json(name = "todayposts")
    val todayPosts: Int? = 0,
    @Json(name = "yesterdayposts")
    val yesterdayPosts: Int? = 0
): Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
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
    ): Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class PostNotice(
        @Json(name = "editthread")
        val editThread: String? = "",
        @Json(name = "editthread_mobile")
        val editThreadMobile: String? = "",
        @Json(name = "newthread")
        val newThread: String? = "",
        @Json(name = "newthread_mobile")
        val newThreadMobile: String? = "",
        @Json(name = "newthread_quick")
        val newThreadQuick: String? = "",
        @Json(name = "poll")
        val poll: String? = "",
        @Json(name = "reply")
        val reply: String? = "",
        @Json(name = "reply_mobile")
        val replyMobile: String? = "",
        @Json(name = "reply_quick")
        val replyQuick: String? = "",
        @Json(name = "reply_quick_mobile")
        val replyQuickMobile: String? = ""
    ): Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ReplyCredit(
        @Json(name = "allowed_credits")
        val allowedCredits: List<String?>? = listOf(),
        @Json(name = "details")
        val details: Details? = Details()
    ): Parcelable {
        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Details(
            @Json(name = "水滴")
            val water: Water? = Water()
        ): Parcelable {
            @Parcelize
            @JsonClass(generateAdapter = true)
            data class Water(
                @Json(name = "max_single_credits")
                val maxSingleCredits: Int? = 0,
                @Json(name = "max_total_credits")
                val maxTotalCredits: Int? = 0,
                @Json(name = "remaining_credits")
                val remainingCredits: Int? = 0,
                @Json(name = "tax_rate")
                val taxRate: Double? = 0.0
            ): Parcelable
        }
    }

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ThreadType(
        @Json(name = "moderators_only")
        val moderatorsOnly: Boolean? = false,
        @Json(name = "name")
        val name: String? = "",
        @Json(name = "type_id")
        val typeId: Int? = 0
    ): Parcelable
}
