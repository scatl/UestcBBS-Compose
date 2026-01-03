package com.scatl.uestcbbs.compose.module.home.entity

import com.scatl.uestcbbs.compose.api.entity.BingDailyPicEntity
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.api.entity.TopListEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewThreadCacheEntity(
    var bannerData: BingDailyPicEntity,
    var homeData: String,
    var newPostData: TopListEntity?,
    var indexData: IndexEntity?
)
