package com.scatl.uestcbbs.compose.api.entity.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2025/2/13 15:12:42
 */
@JsonClass(generateAdapter = true)
data class PostReportRequestEntity(
    @Json(name = "pid")
    var pid: Int,
    @Json(name = "fid")
    var fid: Int,
    @Json(name = "message")
    var message: String = ""
)