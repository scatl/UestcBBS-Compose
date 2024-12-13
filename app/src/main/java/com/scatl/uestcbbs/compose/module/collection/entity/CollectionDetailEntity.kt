package com.scatl.uestcbbs.compose.module.collection.entity

/**
 * Created by sca_tl at 2024/10/11 14:21:55
 */
data class CollectionDetailEntity(
    var collectionId: Int? = 0,
    var collectionTitle: String? = null,
    var isSubscribe: Boolean = false,
    var subscribeCount: String? = null,
    var threadCount: String? = null,
    var ratingScore: Float = 0f,
    var ratingTitle: String? = null,
    var collectionDsp: String? = null,
    var collectionTags: MutableList<String>? = mutableListOf(),
    var maintainer: MutableList<Maintainer> = mutableListOf(),
    var collectionAuthorName: String? = null,
    var collectionAuthorAvatar: String? = null,
    var collectionAuthorId: Int? = 0,
    var collectionAuthorLink: String? = null,

    var postListBean: MutableList<ThreadItem> = mutableListOf(),
    var recentSubscriber: MutableList<RecentSubscriber> = mutableListOf(),
    var authorOtherCollection: MutableList<AuthorOtherCollection> = mutableListOf()
) {
    data class AuthorOtherCollection (
        var cid: Int? = 0,
        var name: String? = null,
        var postCount: Int = 0,
        var subscribeCount: Int = 0,
        var commentCount: Int = 0
    )

    data class ThreadItem (
        var topicTitle: String? = null,
        var topicLink: String? = null,
        var topicId: Int? = 0,
        var hasPic: Boolean = false,
        var hasAttach: Boolean = false,
        var authorName: String? = null,
        var authorId: Int? = 0,
        var authorLink: String? = null,
        var authorAvatar: String? = null,
        var postDate: String? = null,
        var commentCount: String? = null,
        var viewCount: String? = null,
        var lastPostAuthorName: String? = null,
        var lastPostAuthorId: Int? = 0,
        var lastPostAuthorAvatar: String? = null,
        var lastPostAuthorLink: String? = null,
        var lastPostDate: String? = null
    )

    class RecentSubscriber (
        var userName: String? = null,
        var userAvatar: String? = null,
        var userId: Int? = 0
    )

    class Maintainer (
        var userName: String? = null,
        var userAvatar: String? = null,
        var userId: Int? = 0
    )
}