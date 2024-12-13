package com.scatl.uestcbbs.compose.module.magic.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/9/26 9:37:40
 */
data class MagicShopListEntity(
    var dsp: String? = null,
    var itemLists: MutableList<Item> = mutableListOf()
) {
    data class Item(
        var icon: String? = null,
        var name: String? = null,
        var dsp: String? = null,
        var price: String? = null,
        var id: String? = null,
        var outOfStock: Boolean = false
    ): SwipeRefreshItem {
        override var isStickerHeader: Boolean = false
    }
}