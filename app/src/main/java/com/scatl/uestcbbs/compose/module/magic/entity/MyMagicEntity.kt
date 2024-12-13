package com.scatl.uestcbbs.compose.module.magic.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/9/26 17:26:44
 */
data class MyMagicEntity(
    var icon: String? = null,
    var name: String? = null,
    var dsp: String? = null,
    var totalCount: String? = null,
    var totalWeight: String? = null,
    var magicId: String? = null,
    var showUseBtn: Boolean = false
): SwipeRefreshItem {
    override var isStickerHeader: Boolean = false
}