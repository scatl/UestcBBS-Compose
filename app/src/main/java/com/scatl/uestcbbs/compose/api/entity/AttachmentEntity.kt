package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttachmentEntity(
    @Json(name = "attachment_id")
    val attachmentId: Int? = null,
    @Json(name = "dateline")
    val dateline: Int? = 0,
    @Json(name = "description")
    val description: String? = "",
    @Json(name = "download_url")
    val downloadUrl: String? = "",
    @Json(name = "filename")
    val filename: String? = "",
    @Json(name = "is_image")
    val isImage: Int? = 0,
    @Json(name = "path")
    val path: String? = "",
    @Json(name = "picid")
    val picId: Int? = 0,
    @Json(name = "price")
    val price: Int? = 0,
    @Json(name = "size")
    val size: Int? = 0,
    @Json(name = "thumb")
    val thumb: Boolean? = false,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String? = "",
    @Json(name = "width")
    val width: Int? = 0,
    @Json(name = "remote")
    val remote: Int? = 0,
)