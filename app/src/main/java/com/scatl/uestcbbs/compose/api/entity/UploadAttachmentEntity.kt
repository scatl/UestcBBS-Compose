package com.scatl.uestcbbs.compose.api.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadAttachmentEntity(
    @Json(name = "uploaded")
    val uploaded: List<AttachmentEntity>? = listOf()
)