package com.scatl.uestcbbs.compose.module.watertask.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/9/24 17:27:19
 */
data class TaskEntity(
    var type: String? = null,
    var id: Int = 0,
    var dsp: String? = null,
    var popularNum: Int = 0,
    var award: String? = null,
    var name: String? = null,
    var progress: Double = 0.0,
    var icon: String? = null,
    var doneTime: String? = null,
    var failedTime: String? = null,
    var leftTime: String? = null,
    var autoGetAward: Boolean = false
): SwipeRefreshItem {
    override var isStickerHeader: Boolean = false
}