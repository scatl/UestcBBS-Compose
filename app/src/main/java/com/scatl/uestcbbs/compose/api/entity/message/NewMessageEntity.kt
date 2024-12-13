package com.scatl.uestcbbs.compose.api.entity.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/8/20 14:07:22
 */
@JsonClass(generateAdapter = true)
data class NewMessageEntity(
    @Json(name = "chat")
    val chat: Int? = 0,
    @Json(name = "posts")
    val posts: Posts? = Posts(),
    @Json(name = "system")
    val system: System? = System()
) {
    @JsonClass(generateAdapter = true)
    data class Posts(
        @Json(name = "at")
        val at: Int? = 0,
        @Json(name = "comment")
        val comment: Int? = 0,
        @Json(name = "other")
        val other: Int? = 0,
        @Json(name = "rate")
        val rate: Int? = 0,
        @Json(name = "reply")
        val reply: Int? = 0
    )

    @JsonClass(generateAdapter = true)
    data class System(
        @Json(name = "admin")
        val admin: Int? = 0,
        @Json(name = "app")
        val app: Int? = 0,
        @Json(name = "friend")
        val friend: Int? = 0,
        @Json(name = "report")
        val report: Int? = 0,
        @Json(name = "space")
        val space: Int? = 0,
        @Json(name = "system")
        val system: Int? = 0,
        @Json(name = "task")
        val task: Int? = 0
    )
}