package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInEntity(
    @Json(name = "authorization")
    val authorization: String? = "",
    @Json(name = "cookie")
    var cookie: List<String>? = listOf(),
    @Json(name = "uid")
    var uid: String? = String()
)