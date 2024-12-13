package com.scatl.uestcbbs.compose.api.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/7/21 18:50
 */
@JsonClass(generateAdapter = true)
data class CommonThreadEntity(
    @Json(name = "attachment")
    val attachment: Int? = 0,
    @Json(name = "author")
    val author: String? = "",
    @Json(name = "author_id")
    val authorId: Int? = 0,
    @Json(name = "can_reply")
    val canReply: Boolean? = false,
    @Json(name = "collections")
    val collections: List<CollectionEntity>? = null,
    @Json(name = "comments")
    val comments: Int? = 0,
    @Json(name = "cover")
    val cover: Int? = 0,
    @Json(name = "dateline")
    val dateline: Int? = 0,
    @Json(name = "display_order")
    val displayOrder: Int? = 0,
    @Json(name = "favorite_times")
    val favoriteTimes: Int? = 0,
    @Json(name = "forum_id")
    val forumId: Int? = 0,
    @Json(name = "forum_name")
    val forumName: String? = "",
    @Json(name = "has_stick_reply")
    val hasStickReply: Int? = 0,
    @Json(name = "heats")
    val heats: Int? = 0,
    @Json(name = "icon")
    val icon: Int? = 0,
    @Json(name = "is_closed")
    val isClosed: Int? = 0,
    @Json(name = "is_moderated")
    val isModerated: Int? = 0,
    @Json(name = "is_rated")
    val isRated: Int? = 0,
    @Json(name = "last_moderation")
    val lastModeration: LastModeration? = LastModeration(),
    @Json(name = "last_post")
    val lastPost: Int? = 0,
    @Json(name = "last_poster")
    val lastPoster: String? = "",
    @Json(name = "max_position")
    val maxPosition: Int? = 0,
    @Json(name = "recommend_add")
    val recommendAdd: Int? = 0,
    @Json(name = "recommend_sub")
    val recommendSub: Int? = 0,
    @Json(name = "recommends")
    val recommends: Int? = 0,
    @Json(name = "replies")
    val replies: Int? = 0,
    @Json(name = "share_times")
    val shareTimes: Int? = 0,
    @Json(name = "special")
    val special: Int? = 0,
    @Json(name = "stamp")
    val stamp: Int? = 0,
    @Json(name = "status")
    val status: Int? = 0,
    @Json(name = "subject")
    val subject: String? = "",
    @Json(name = "summary")
    val summary: String? = "",
    @Json(name = "summary_attachments")
    val summaryAttachments: List<AttachmentEntity>? = listOf(),
    @Json(name = "thread_id")
    val threadId: Int? = 0,
    @Json(name = "type_id")
    val typeId: Int? = 0,
    @Json(name = "views")
    val views: Int? = 0,
    @Json(name = "digest")
    val digest: Int? = 0,
    @Json(name = "reply_credit_remaining_amount")
    val replyAward: Int? = 0,
    @Json(name = "highlight_color")
    val highLightColor: String? = null,
    @Json(name = "highlight_bold")
    val highLightBold: Boolean? = null,
    @Json(name = "highlight_italic")
    val highLightItalic: Boolean? = null,
    @Json(name = "highlight_underline")
    val highLightUnderline: Boolean? = null,
    @Json(name = "poll")
    var poll: ThreadPollEntity? = null,
    @Json(name = "reply_credit")
    val replyCredit: ReplyCredit? = null,
    @Json(name = "rush_reply")
    val rushReply: RushReply? = null,

    var postId: Int? = 0,
    var summaries: MutableList<String>? = null
) : SwipeRefreshItem {
    override var isStickerHeader = false

    @JsonClass(generateAdapter = true)
    data class LastModeration(
        @Json(name = "action")
        val action: String? = "",
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "uid")
        val uid: Int? = 0,
        @Json(name = "username")
        val username: String? = ""
    )

    @JsonClass(generateAdapter = true)
    data class ReplyCredit(
        @Json(name = "count")
        val count: Int? = 0,
        @Json(name = "credit_amount")
        val creditAmount: Int? = 0,
        @Json(name = "credit_name")
        val creditName: String? = "",
        @Json(name = "limit_per_user")
        val limitPerUser: Int? = 0,
        @Json(name = "probability")
        val probability: Int? = 0,
        @Json(name = "remaining_amount")
        val remainingAmount: Int? = 0
    )

    @JsonClass(generateAdapter = true)
    data class RushReply(
        @Json(name = "award_limits")
        val awardLimits: Any? = Any(),
        @Json(name = "award_uids")
        val awardUids: Any? = Any(),
        @Json(name = "credit_limit")
        val creditLimit: Int? = 0,
        @Json(name = "end_time")
        val endTime: Int? = 0,
        @Json(name = "excluded_uids")
        val excludedUids: Any? = Any(),
        @Json(name = "max_consecutive_replies")
        val maxConsecutiveReplies: Int? = 0,
        @Json(name = "max_position")
        val maxPosition: Int? = 0,
        @Json(name = "start_time")
        val startTime: Int? = 0,
        @Json(name = "target_positions")
        val targetPositions: List<String>? = listOf()
    )
}