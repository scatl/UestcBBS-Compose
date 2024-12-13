package com.scatl.uestcbbs.compose.module.collection.entity

import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefreshItem

/**
 * Created by sca_tl at 2024/10/11 9:33:39
 */
data class CollectionListEntity(
    var collectionLink: String? = null, //专辑链接
    var collectionDsp: String? = null, //专辑描述
    var collectionId: Int = 0, //专辑id
    var postCount: String? = null, //主题数
    var collectionTitle: String? = null, //专辑标题
    var authorId: Int = 0, //作者id
    var authorName: String? = null, //作者昵称
    var authorLink: String? = null, //作者链接
    var authorAvatar: String? = null, //作者头像
    var subscribeCount: String? = null, //订阅数
    var commentCount: String? = null, //评论数
    var latestUpdateDate: String? = null, //最近更新时间
    var latestPostTitle: String? = null, //最新主题标题
    var latestPostLink: String? = null, //最新主题链接
    var latestPostId: Int = 0, //最新主题id
    var collectionTags: MutableList<String> = mutableListOf(),
    var createByMe: Boolean = false,
    var subscribeByMe: Boolean = false,
    var hasUnreadPost: Boolean = false
): SwipeRefreshItem {
    override var isStickerHeader: Boolean = false
}