package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2025/2/8 16:34:53
 */
@JsonClass(generateAdapter = true)
data class RateRequestEntity(
    @Json(name = "notify")
    var notify: Boolean = true,
    @Json(name = "reason")
    var reason: String? = "",
    @Json(name = "credits")
    var credits: Credit? = Credit(),
) {
    @JsonClass(generateAdapter = true)
    data class Credit(
        @Json(name = "水滴")
        var water: Int? = 0,
    )
}