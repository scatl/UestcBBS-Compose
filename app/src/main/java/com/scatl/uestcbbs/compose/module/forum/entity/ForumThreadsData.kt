package com.scatl.uestcbbs.compose.module.forum.entity

import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/7/24 17:53:11
 */
sealed class ForumThreadsData: SwipeRefreshItem {

    enum class ForumThreadsDataType(name: String) {
        FILTER("filter"),
        STICK("stick"),
        THREAD_TITLE("thread_title"),
        THREAD("thread")
    }

    open var itemType: ForumThreadsDataType = ForumThreadsDataType.FILTER
    override var isStickerHeader = false

    class Filter(
        var data: ForumDetailEntity,
        override var itemType: ForumThreadsDataType = ForumThreadsDataType.FILTER,
        override var isStickerHeader: Boolean = true
    ): ForumThreadsData()

    class Stick(
        var data: List<CommonThreadEntity>,
        override var itemType: ForumThreadsDataType = ForumThreadsDataType.STICK,
        override var isStickerHeader: Boolean = false
    ): ForumThreadsData()

    class ThreadTitle(
        var data: String,
        override var itemType: ForumThreadsDataType = ForumThreadsDataType.THREAD_TITLE,
        override var isStickerHeader: Boolean = false
    ): ForumThreadsData()

    class Thread(
        var data: CommonThreadEntity,
        override var itemType: ForumThreadsDataType = ForumThreadsDataType.THREAD
    ): ForumThreadsData()

}