package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2025/2/8 14:27:36
 */
@JsonClass(generateAdapter = true)
data class RateOptionsEntity(
    @Json(name = "common_reasons")
    var commonReasons: List<String?>? = listOf(),
    @Json(name = "credits")
    var credits: Credits? = Credits(),
    @Json(name = "require_notify")
    var requireNotify: Boolean? = false
) {
    @JsonClass(generateAdapter = true)
    data class Credits(
        @Json(name = "水滴")
        var water: Water? = Water()
    ) {
        @JsonClass(generateAdapter = true)
        data class Water(
            @Json(name = "deduct_self")
            var deductSelf: Boolean? = false,
            @Json(name = "limit_24h_negative")
            var limit24hNegative: Int? = 0,
            @Json(name = "limit_24h_positive")
            var limit24hPositive: Int? = 0,
            @Json(name = "max")
            var max: Int? = 0,
            @Json(name = "min")
            var min: Int? = 0,
            @Json(name = "remaining_24h_negative")
            var remaining24hNegative: Int? = 0,
            @Json(name = "remaining_24h_positive")
            var remaining24hPositive: Int? = 0,
            @Json(name = "tax_rate_negative")
            var taxRateNegative: Double? = 0.0
        )
    }
}