package com.scatl.uestcbbs.compose.module.home.newpost.entity

import com.scatl.uestcbbs.compose.api.entity.BingDailyPicEntity
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/7/11 9:27:03
 */
sealed class NewThreadData: SwipeRefreshItem {

    enum class NewThreadDataType(name: String) {
        BANNER("banner"),
        NEW_THREAD("new_thread"),
        SITE_STATUS("site_status")
    }

    open var itemType: NewThreadDataType = NewThreadDataType.BANNER
    override var isStickerHeader = false

    class Banner(
        var data: List<BingDailyPicEntity.Image>,
        override var itemType: NewThreadDataType = NewThreadDataType.BANNER
    ): NewThreadData()

    class SiteStatus(
        var data: IndexEntity,
        override var itemType: NewThreadDataType = NewThreadDataType.SITE_STATUS
    ): NewThreadData()

    class NewThread(
        var data: CommonThreadEntity,
        override var itemType: NewThreadDataType = NewThreadDataType.NEW_THREAD
    ): NewThreadData()

}