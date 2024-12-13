package com.scatl.uestcbbs.compose.api.entity.request

import android.os.Parcelable
import com.scatl.uestcbbs.compose.api.entity.AttachmentEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Created by sca_tl at 2024/11/8 20:40:13
 */
@JsonClass(generateAdapter = true)
data class CreatePostRequestEntity(
    var attachments: MutableList<AttachmentEntity> = mutableListOf(),
    var format: Int = 2,
    var message: String = "",
    var usesig: Int = 1,

    @Json(name = "forum_id")
    var forumId: Int? = null,
    @Json(name = "type_id")
    var typeId: Int? = null,
    @Json(name = "thread_id")
    var threadId: Int? = null,
    @Json(name = "post_id")
    var postId: Int? = null,
    @Json(name = "is_anonymous")
    var anonymous: Boolean? = false,
    @Json(name = "poll")
    val poll: Poll? = null,
    @Json(name = "reply_credit")
    val replyCredit: ReplyCredit? = null
) {
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Poll(
        @Json(name = "expiration")
        var expiration: Int = 3,
        @Json(name = "is_image")
        var isImage: Boolean = false,
        @Json(name = "max_choices")
        var maxChoices: Int = 1,
        @Json(name = "options")
        var options: List<Option> = listOf(Option.obtain(), Option.obtain()),
        @Json(name = "show_voters")
        var showVoters: Boolean = false,
        @Json(name = "visible")
        var visible: Boolean = false
    ): Parcelable {

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Option(
            @Json(name = "text")
            var text: String = "",
            @Json(ignore = true)
            var key: String = ""
        ): Parcelable {

            companion object {
                @OptIn(ExperimentalUuidApi::class)
                fun obtain() = Option("", Uuid.random().toString())
            }

        }
    }

    @Parcelize
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
        val probability: Int? = 0
    ): Parcelable
}