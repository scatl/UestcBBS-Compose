package com.scatl.uestcbbs.compose.api.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostCommentAndRateEntity(
    @Json(name = "comment_page_size")
    val commentPageSize: Int? = 0,
    @Json(name = "comment_pages")
    val commentPages: Int? = 0,
    @Json(name = "comment_total")
    var commentTotal: Int? = 0,
    @Json(name = "comments")
    val comments: MutableList<Comment> = mutableListOf(),
    @Json(name = "rate_stat")
    val rateStat: RateStat = RateStat(),
    @Json(name = "rates")
    val rates: MutableList<Rate> = mutableListOf()
) {
    @JsonClass(generateAdapter = true)
    data class Comment(
        @Json(name = "author")
        var author: String? = "",
        @Json(name = "author_id")
        var authorId: Int? = 0,
        @Json(name = "dateline")
        var dateline: Int? = 0,
        @Json(name = "id")
        var id: Int? = 0,
        @Json(name = "message")
        var message: String? = "",

        override var isStickerHeader: Boolean = false
    ): SwipeRefreshItem

    @JsonClass(generateAdapter = true)
    data class RateStat(
        @Json(name = "total_credits")
        val totalCredits: TotalCredits = TotalCredits(),
        @Json(name = "total_users")
        val totalUsers: Int? = 0
    ) {
        @JsonClass(generateAdapter = true)
        data class TotalCredits(
            @Json(name = "水滴")
            val water: Int? = 0,
            @Json(name = "威望")
            val weiWang: Int? = 0
        )
    }

    @JsonClass(generateAdapter = true)
    data class Rate(
        @Json(name = "credits")
        val credits: Credits? = Credits(),
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "reason")
        val reason: String? = "",
        @Json(name = "user_id")
        val userId: Int? = 0,
        @Json(name = "username")
        val username: String? = "",

        override var isStickerHeader: Boolean = false
    ): SwipeRefreshItem {
        @JsonClass(generateAdapter = true)
        data class Credits(
            @Json(name = "水滴")
            val water: Int? = 0,
            @Json(name = "威望")
            val weiWang: Int? = 0
        )
    }
}