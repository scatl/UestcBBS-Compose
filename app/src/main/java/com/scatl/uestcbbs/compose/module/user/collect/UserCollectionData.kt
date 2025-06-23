package com.scatl.uestcbbs.compose.module.user.collect

import com.scatl.uestcbbs.compose.api.entity.CollectionEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserCollectionEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/7/24 17:53:11
 */
sealed class UserCollectionData: SwipeRefreshItem {

    enum class UserCollectionDataType(name: String) {
        COLLECTION_TITLE("collection_title"),
        COLLECTION("collection"),
        THREAD_TITLE("thread_title"),
        THREAD("thread")
    }

    open var itemType: UserCollectionDataType = UserCollectionDataType.COLLECTION_TITLE
    override var isStickerHeader = false

    class CollectionTitle(
        var data: String,
        override var itemType: UserCollectionDataType = UserCollectionDataType.COLLECTION_TITLE,
        override var isStickerHeader: Boolean = false
    ): UserCollectionData()

    class Collection(
        var data: CollectionEntity,
        override var itemType: UserCollectionDataType = UserCollectionDataType.COLLECTION,
        override var isStickerHeader: Boolean = false
    ): UserCollectionData()

    class ThreadTitle(
        var data: String,
        override var itemType: UserCollectionDataType = UserCollectionDataType.THREAD_TITLE,
        override var isStickerHeader: Boolean = false
    ): UserCollectionData()

    class Thread(
        var data: UserCollectionEntity.Row,
        override var itemType: UserCollectionDataType = UserCollectionDataType.THREAD
    ): UserCollectionData()

}