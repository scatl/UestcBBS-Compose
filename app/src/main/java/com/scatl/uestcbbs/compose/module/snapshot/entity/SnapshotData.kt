package com.scatl.uestcbbs.compose.module.snapshot.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/9/14 16:26:07
 */
data class SnapshotData(
    var tid: String,
    var subject: String,
    var snapshots: MutableList<String>
): SwipeRefreshItem {
    override var isStickerHeader: Boolean = false
}