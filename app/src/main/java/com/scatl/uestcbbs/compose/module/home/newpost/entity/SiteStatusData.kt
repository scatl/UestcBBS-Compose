package com.scatl.uestcbbs.compose.module.home.newpost.entity

import com.scatl.uestcbbs.compose.api.entity.IndexEntity

data class SiteStatusData(
    var indexEntity: IndexEntity,
    var onlineNum: Int = 0
)
