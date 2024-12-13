package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BingDailyPicEntity(
    @Json(name = "images")
    val images: List<Image>? = listOf(),
    @Json(name = "tooltips")
    val tooltips: Tooltips? = Tooltips()
) {
    @JsonClass(generateAdapter = true)
    data class Image(
        @Json(name = "bot")
        val bot: Int? = 0,
        @Json(name = "copyright")
        val copyright: String? = "",
        @Json(name = "copyrightlink")
        val copyrightlink: String? = "",
        @Json(name = "drk")
        val drk: Int? = 0,
        @Json(name = "enddate")
        val enddate: String? = "",
        @Json(name = "fullstartdate")
        val fullstartdate: String? = "",
        @Json(name = "hs")
        val hs: List<Any?>? = listOf(),
        @Json(name = "hsh")
        val hsh: String? = "",
        @Json(name = "quiz")
        val quiz: String? = "",
        @Json(name = "startdate")
        val startdate: String? = "",
        @Json(name = "title")
        val title: String? = "",
        @Json(name = "top")
        val top: Int? = 0,
        @Json(name = "url")
        val url: String? = "",
        @Json(name = "urlbase")
        val urlbase: String? = "",
        @Json(name = "wp")
        val wp: Boolean? = false,

        var fullThumbUrl: String?,
        var fullOriginUrl: String?
    )

    @JsonClass(generateAdapter = true)
    data class Tooltips(
        @Json(name = "loading")
        val loading: String? = "",
        @Json(name = "next")
        val next: String? = "",
        @Json(name = "previous")
        val previous: String? = "",
        @Json(name = "walle")
        val walle: String? = "",
        @Json(name = "walls")
        val walls: String? = ""
    )
}