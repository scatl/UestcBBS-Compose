package com.scatl.uestcbbs.compose.module.home.newpost.entity

import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SiteStatusData(
    var indexEntity: IndexEntity,
    var onlineNum: Int = 0
)
