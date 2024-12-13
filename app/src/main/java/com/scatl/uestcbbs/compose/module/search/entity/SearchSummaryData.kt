package com.scatl.uestcbbs.compose.module.search.entity

import com.scatl.uestcbbs.compose.api.entity.search.SearchSummaryEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/9/12 17:31:49
 */
sealed class SearchSummaryData: SwipeRefreshItem {

    enum class SearchSummaryDataType(name: String) {
        TITLE("title"),
        THREAD("thread"),
        User("user")
    }

    open var itemType: SearchSummaryDataType = SearchSummaryDataType.TITLE
    override var isStickerHeader = false

    class Title(
        var data: TitleData,
        override var isStickerHeader: Boolean = true,
        override var itemType: SearchSummaryDataType = SearchSummaryDataType.TITLE
    ): SearchSummaryData()

    class Thread(
        var data: SearchSummaryEntity.Thread,
        override var itemType: SearchSummaryDataType = SearchSummaryDataType.THREAD
    ): SearchSummaryData()

    class User(
        var data: SearchSummaryEntity.User,
        override var itemType: SearchSummaryDataType = SearchSummaryDataType.User
    ): SearchSummaryData()


    data class TitleData(
        var count: Int,
        var type: SearchSummaryDataType
    )
}