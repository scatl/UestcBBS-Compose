package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/7/25 20:47
 */
@JsonClass(generateAdapter = true)
data class CollectionEntity(
    @Json(name = "average_rate")
    val averageRate: Float? = 0f,
    @Json(name = "collection_id")
    val collectionId: Int? = 0,
    @Json(name = "comments")
    val comments: Int? = 0,
    @Json(name = "dateline")
    val dateline: Int? = 0,
    @Json(name = "description")
    val description: String? = "",
    @Json(name = "follows")
    val follows: Int? = 0,
    @Json(name = "is_owner")
    val isOwner: Boolean? = false,
    @Json(name = "keyword")
    val keyword: String? = "",
    @Json(name = "last_update")
    val lastUpdate: Int? = 0,
    @Json(name = "last_visit")
    val lastVisit: Int? = 0,
    @Json(name = "latest_thread")
    val latestThread: LatestThread? = LatestThread(),
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "rates")
    val rates: Int? = 0,
    @Json(name = "threads")
    val threads: Int? = 0,
    @Json(name = "uid")
    val uid: Int? = 0,
    @Json(name = "username")
    val username: String? = ""
) {
    @JsonClass(generateAdapter = true)
    data class LatestThread(
        @Json(name = "dateline")
        val dateline: Int? = 0,
        @Json(name = "lastpost_author")
        val lastpostAuthor: String? = "",
        @Json(name = "subject")
        val subject: String? = "",
        @Json(name = "thread_id")
        val threadId: Int? = 0
    )
}