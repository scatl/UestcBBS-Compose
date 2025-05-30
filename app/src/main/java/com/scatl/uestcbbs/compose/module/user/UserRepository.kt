package com.scatl.uestcbbs.compose.module.user

import com.scatl.uestcbbs.compose.api.entity.request.AddFriendRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.EditFriendRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.UserAddCommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.UserEditCommentRequestEntity
import com.scatl.uestcbbs.compose.api.service.UserService
import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/15 10:05:05
 */
class UserRepository @Inject constructor(): BaseRepository() {

    suspend fun getUserProfileByUid(
        id: String?,
        additional: String? = null
    ) = userService.getUserProfileByUid(id, additional)

    suspend fun getUserProfileByName(
        name: String?,
        additional: String? = null
    ) = userService.getUserProfileByName(name, additional)

    suspend fun getUserPostsByUid(
        postType: UserService.UserPostType,
        uid: String?,
        userSummary: String?,
        visitors: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserPostByUid(postType, uid, userSummary, visitors, page, additional)

    suspend fun getUserPostsByName(
        postType: UserService.UserPostType,
        name: String?,
        userSummary: String?,
        visitors: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserPostByName(postType, name, userSummary, visitors, page, additional)

    suspend fun getUserDigests(
        author: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserDigested(author, page, additional)

    suspend fun getUserMsgBoard(
        uid: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserMessageBoard(uid, page, additional)

    suspend fun getUserCollectionByUid(
        uid: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserCollectionByUid(uid, page, additional)

    suspend fun getUserCollectionByName(
        name: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserCollectionByName(name, page, additional)

    suspend fun addFriend(
        requestEntity: AddFriendRequestEntity,
        additional: String? = null
    ) = userService.addFriend(requestEntity, additional)

    suspend fun getUserFriendsByUid(
        uid: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserFriendsById(uid, page, additional)

    suspend fun getUserFriendsByName(
        name: String?,
        page: Int,
        additional: String? = null
    ) = userService.getUserFriendsByName(name, page, additional)

    suspend fun editFriend(
        uid: String,
        requestEntity: EditFriendRequestEntity
    ) = userService.editFriend(uid, requestEntity)

    suspend fun deleteFriend(
        uid: String
    ) = userService.deleteFriend(uid)

    suspend fun addComment(
        requestBody: UserAddCommentRequestEntity
    ) = userService.addComment(requestBody)

    suspend fun editComment(
        id: String,
        requestBody: UserEditCommentRequestEntity
    ) = userService.editComment(id, requestBody)

    suspend fun deleteComment(
        id: String
    ) = userService.deleteComment(id)

    suspend fun getUserSpace(
        id: String,
        `do`: String
    ) = legacyService.userSpace(id, `do`)
}