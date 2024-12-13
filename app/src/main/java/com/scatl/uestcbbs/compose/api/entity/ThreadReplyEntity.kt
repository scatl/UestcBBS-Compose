package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/11/13 11:12:16
 */
@JsonClass(generateAdapter = true)
data class ThreadReplyEntity(
    @Json(name = "post_id")
    val postId: Int? = 0,
    @Json(name = "ext_credits_update")
    val extCreditsUpdate: ExtCreditsUpdate? = null,
) {
    @JsonClass(generateAdapter = true)
    data class ExtCreditsUpdate(
        @Json(name = "水滴")
        val water: Int? = 0,
        @Json(name = "威望")
        val prestige: Int? = 0,
        @Json(name = "奖励券")
        val coupons: Int? = 0
    )
}