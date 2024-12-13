package com.scatl.uestcbbs.compose.module.forum.entity

import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/7/11 15:44:25
 */
sealed class ForumCategoryData: SwipeRefreshItem {

    enum class ForumCategoryDataType(name: String) {
        TITLE("title"),
        CHILDREN("children")
    }

    open var itemType: ForumCategoryDataType = ForumCategoryDataType.TITLE
    override var isStickerHeader = false

    class Title(
        var data: IndexEntity.Forum,
        override var itemType: ForumCategoryDataType = ForumCategoryDataType.TITLE,
        override var isStickerHeader: Boolean = true
    ): ForumCategoryData()

    class Children(
        var data: IndexEntity.Forum,
        override var itemType: ForumCategoryDataType = ForumCategoryDataType.CHILDREN
    ): ForumCategoryData()

}