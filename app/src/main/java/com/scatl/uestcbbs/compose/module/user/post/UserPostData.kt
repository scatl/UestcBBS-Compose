package com.scatl.uestcbbs.compose.module.user.post

import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/7/21 14:16
 */
sealed class UserPostData: SwipeRefreshItem {

    enum class UserPostDataType(name: String) {
        THREAD("thread"),
        REPLY("reply"),
        COMMENT("comment"),
        FEATURED("featured"),
    }

    open var itemType: UserPostDataType = UserPostDataType.THREAD
    override var isStickerHeader = false

    class Thread(
        var data: CommonThreadEntity,
        override var itemType: UserPostDataType = UserPostDataType.THREAD
    ): UserPostData()

    class Reply(
        var data: CommonThreadEntity,
        override var itemType: UserPostDataType = UserPostDataType.REPLY
    ): UserPostData()

    class Comment(
        var data: CommonThreadEntity,
        override var itemType: UserPostDataType = UserPostDataType.COMMENT
    ): UserPostData()

    class Featured(
        var data: CommonThreadEntity,
        override var itemType: UserPostDataType = UserPostDataType.FEATURED
    ): UserPostData()
}