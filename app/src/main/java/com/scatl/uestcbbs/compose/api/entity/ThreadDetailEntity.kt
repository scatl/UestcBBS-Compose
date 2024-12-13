package com.scatl.uestcbbs.compose.api.entity

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.widget.image.picker.MediaEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
data class ThreadDetailEntity(
    @Json(name = "forum")
    val forum: ForumDetailEntity? = ForumDetailEntity(),
    @Json(name = "page")
    val page: Int? = 0,
    @Json(name = "page_size")
    val pageSize: Int? = 0,
    @Json(name = "rows")
    var rows: MutableList<Row>? = mutableListOf(),
    @Json(name = "thread")
    val thread: CommonThreadEntity? = CommonThreadEntity(),
    @Json(name = "total")
    val total: Int? = 0,
) {
    @JsonClass(generateAdapter = true)
    data class Row(
        @Json(name = "attachments")
        val attachments: List<AttachmentEntity>? = listOf(),
        @Json(name = "author")
        val author: String? = "",
        @Json(name = "author_details")
        val authorDetails: AuthorDetails? = null,
        @Json(name = "author_id")
        val authorId: Int? = 0,
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "format")
        val format: Int? = 0,
        @Json(name = "forum_id")
        val forumId: Int? = 0,
        @Json(name = "has_comment")
        val hasComment: Boolean? = false,
        @Json(name = "has_rate")
        val hasRate: Boolean? = false,
        @Json(name = "is_anonymous")
        val isAnonymous: Int? = 0,
        @Json(name = "is_first")
        val isFirst: Int? = 0,
        @Json(name = "message")
        val message: String? = "",
        @Json(name = "oppose")
        val oppose: Int? = 0,
        @Json(name = "parseurloff")
        val parseUrlOff: Int? = 0,
        @Json(name = "pinned")
        val pinned: Boolean? = false,
        @Json(name = "position")
        val position: Int? = 0,
        @Json(name = "post_id")
        val postId: Int? = 0,
        @Json(name = "smileyoff")
        val smileyOff: Int? = 0,
        @Json(name = "status")
        val status: Int? = 0,
        @Json(name = "subject")
        val subject: String? = "",
        @Json(name = "support")
        val support: Int? = 0,
        @Json(name = "thread_id")
        val threadId: Int? = 0,
        @Json(name = "usesig")
        val useSig: Int? = 0,
        @Json(name = "reply_credit_amount")
        val replyCreditAmount: Int? = 0,
        @Json(name = "reply_credit_name")
        val replyCreditName: String? = "",
        @Json(name = "blocked")
        val blocked: Boolean?,
        @Json(name = "warned")
        val warned: Boolean?,

        var commentAndRate: PostCommentAndRateEntity = PostCommentAndRateEntity(),

        @Json(ignore = true)
        val highLight: MutableStateFlow<Boolean> = MutableStateFlow(false),

        override var isStickerHeader: Boolean = false
    ): SwipeRefreshItem {

        override fun equals(other: Any?): Boolean {
            if (this === other) { return true }
            if (other !is Row) { return false }
            if (postId != null) { return postId == other.postId }
            return false
        }

        override fun hashCode(): Int {
            var result = postId?.hashCode() ?: 0
            return result
        }

        @JsonClass(generateAdapter = true)
        data class AuthorDetails(
            @Json(name = "credits")
            val credits: Int? = 0,
            @Json(name = "custom_title")
            val customTitle: String? = "",
            @Json(name = "digests")
            val digests: Int? = 0,
            @Json(name = "ext_credits")
            val extCredits: ExtCredits? = ExtCredits(),
            @Json(name = "group_icon")
            val groupIcon: String? = "",
            @Json(name = "group_id")
            val groupId: Int? = 0,
            @Json(name = "group_subtitle")
            val groupSubtitle: String? = "",
            @Json(name = "group_title")
            val groupTitle: String? = "",
            @Json(name = "last_visit")
            val lastVisit: Int? = 0,
            @Json(name = "level_id")
            val levelId: Int? = 0,
            @Json(name = "medals")
            val medals: List<Int>? = listOf(),
            @Json(name = "online_time")
            val onlineTime: Int? = 0,
            @Json(name = "posts")
            val posts: Int? = 0,
            @Json(name = "register_time")
            val registerTime: Int? = 0,
            @Json(name = "signature")
            val signature: String? = "",
            @Json(name = "signature_format")
            val signatureFormat: String? = ""
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
}