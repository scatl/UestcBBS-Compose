package com.scatl.uestcbbs.compose.api.entity.wealth

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

data class WealthHistoryEntity(
    var action: String? = "",
    var change: String? = "",
    var detail: String? = "",
    var time: String? = "",
    var link: String? = "",
    var increase: Boolean = false,
    override var isStickerHeader: Boolean = false
): SwipeRefreshItem
