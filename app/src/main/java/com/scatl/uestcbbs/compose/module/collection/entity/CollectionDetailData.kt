package com.scatl.uestcbbs.compose.module.collection.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/10/11 14:33:44
 */
sealed class CollectionDetailData: SwipeRefreshItem {

    enum class CollectionDetailDataType(name: String) {
        INFO("info"),
        THREAD("thread")
    }

    open var itemType: CollectionDetailDataType = CollectionDetailDataType.INFO
    override var isStickerHeader = false

    class Info(
        var data: CollectionDetailEntity,
        override var itemType: CollectionDetailDataType = CollectionDetailDataType.INFO,
    ): CollectionDetailData()

    class Thread(
        var data: CollectionDetailEntity.ThreadItem,
        override var itemType: CollectionDetailDataType = CollectionDetailDataType.THREAD
    ): CollectionDetailData()

}