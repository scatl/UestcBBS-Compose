package com.scatl.uestcbbs.compose.module.setting.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2024/10/12 16:25:59
 */
@JsonClass(generateAdapter = true)
data class OpenSourceEntity(
    var name: String,
    var author: String,
    var link: String,
    var description: String
): SwipeRefreshItem {
    override var isStickerHeader: Boolean = false
}