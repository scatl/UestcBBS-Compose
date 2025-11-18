package com.scatl.uestcbbs.compose.router

import com.scatl.uestcbbs.compose.module.post.commentrate.CommentRateType
import kotlinx.serialization.Serializable

/**
 * Created by sca_tl at 2024/4/10 13:34:50
 */
object Router {

    @Serializable object AddAccountRouterEntity
    @Serializable object AccountManageRouterEntity
    @Serializable object MainRouterEntity
    @Serializable object SettingRouterEntity
    @Serializable object AboutRouterEntity
    @Serializable object OpenSourceRouterEntity
    @Serializable object SnapshotRouterEntity
    @Serializable object DayQuestionRouterEntity
    @Serializable object SearchRouterEntity
    @Serializable object HistoryRouterEntity
    @Serializable object MagicShopRouterEntity
    @Serializable object MedalRouterEntity
    @Serializable object CreateThreadRouterEntity
    @Serializable object WaterTaskRouterEntity
    @Serializable object DarkRoomRouterEntity
    @Serializable object MyWealthRouterEntity
    @Serializable object WealthHistoryRouterEntity

    @Serializable
    data class ChatDetailRouterEntity(
        var uid: Int,
        var name: String
    )

    @Serializable
    data class CollectionDetailRouterEntity(
        var id: Int
    )

    @Serializable
    data class CollectionListRouterEntity(
        var create: Boolean = false
    )

    @Serializable
    data class DownloadRouterEntity(
        var url: String? = "",
        var name: String? = ""
    )

    @Serializable
    data class ForumDetailRouterEntity(
        var fid: Int
    )

    @Serializable
    data class ImageViewerRouterEntity(
        var configId: String? = ""
    )

    @Serializable
    data class MediaPickerRouterEntity(
        var config: String
    )

    @Serializable
    data class PostCommentAndRateRouterEntity(
        var tid: Int,
        var pid: Int,
        var tab: String? = CommentRateType.RATE.name
    )

    @Serializable
    data class ThreadDetailRouterEntity(
        val snapshot: String? = null,
        val id: Int,
        val pid: Int? = null
    )

    @Serializable
    data class UserProfileRouterEntity(
        var uid: Int?,
        var name: String
    )

}