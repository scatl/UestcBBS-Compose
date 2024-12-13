package com.scatl.uestcbbs.compose.module.medal.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/10/8 14:13:14
 */
data class MedalListEntity(
    var medalList: MutableList<MedalItem> = mutableListOf(),
    var medalHistory: MutableList<MedalHistory> = mutableListOf()
) {
    data class MedalItem (
        var medalName: String? = null,
        var medalDsp: String? = null,
        var medalId: Int = 0,
        var medalIcon: String? = null,
        var buyDsp: String? = null,
        var alreadyOwn: Boolean = false
    ): SwipeRefreshItem {
        override var isStickerHeader: Boolean = false
    }

    data class MedalHistory (
        var userName: String? = null,
        var userId: Int = 0,
        var userAvatar: String? = null,
        var dsp: String? = null
    )
}