package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/7/12 10:33:34
 */
@JsonClass(generateAdapter = true)
data class LoginRequestEntity(
    @Json(name = "username")
    var username: String,
    @Json(name = "password")
    var password: String,
    @Json(name = "keep_signed_in")
    var keepSignedIn: Boolean = true
)