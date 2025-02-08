package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.MessageBoardEntity
import com.scatl.uestcbbs.compose.api.entity.request.AddFriendRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.DeleteFavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.EditFriendRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.UserAddCommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.UserEditCommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.user.AddCommentEntity
import com.scatl.uestcbbs.compose.api.entity.user.AddFriendEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserCollectionEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserFriendEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserPostEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/15 15:43:14
 */
interface UserService {

    enum class UserPostType(val type: String) {
        THREAD("threads"),
        REPLY("replies"),
        COMMENT("postcomments"),
        FEATURED("featured");

        override fun toString() = type
    }

    @GET("user/{id}/profile?user_summary=1&visitors=1")
    suspend fun getUserProfileByUid(
        @Path("id") id: String?,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserProfileEntity>

    @GET("user/name/{name}/profile?user_summary=1&visitors=1")
    suspend fun getUserProfileByName(
        @Path("name") name: String?,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserProfileEntity>

    @GET("user/{id}/{postType}")
    suspend fun getUserPostByUid(
        @Path("postType") postType: UserPostType?,
        @Path("id") id: String?,
        @Query("user_summary") userSummary: String?,
        @Query("visitors") visitors: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserPostEntity>

    @GET("user/name/{name}/{postType}")
    suspend fun getUserPostByName(
        @Path("postType") postType: UserPostType?,
        @Path("name") name: String?,
        @Query("user_summary") userSummary: String?,
        @Query("visitors") visitors: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserPostEntity>

    @GET("search/threads?digest=1")
    suspend fun getUserDigested(
        @Query("author") author: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserPostEntity>

    @GET("user/{id}/comments")
    suspend fun getUserMessageBoard(
        @Path("id") uid: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<MessageBoardEntity>

    @GET("user/{id}/favorites?collections=1")
    suspend fun getUserCollectionByUid(
        @Path("id") id: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserCollectionEntity>

    @GET("user/name/{name}/favorites?collections=1")
    suspend fun getUserCollectionByName(
        @Path("name") name: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserCollectionEntity>

    @PUT("user/friend")
    suspend fun addFriend(
        @Body requestBody: AddFriendRequestEntity,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<AddFriendEntity> //requested

    @GET("user/{id}/friends")
    suspend fun getUserFriendsById(
        @Path("id") id: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserFriendEntity>

    @GET("user/name/{name}/friends")
    suspend fun getUserFriendsByName(
        @Path("name") name: String?,
        @Query("page") page: Int,
        @Query("additional") additional: String? // removevlog
    ): BaseApiResult<UserFriendEntity>

    @PATCH("user/friend/{id}")
    suspend fun editFriend(
        @Path("id") id: String?,
        @Body requestBody: EditFriendRequestEntity
    ): BaseApiResult<Any?>

    @DELETE("user/friend/{id}")
    suspend fun deleteFriend(
        @Path("id") id: String?
    ): BaseApiResult<Any?>

    @POST("user/comment")
    suspend fun addComment(
        @Body requestBody: UserAddCommentRequestEntity
    ): BaseApiResult<AddCommentEntity>

    @PATCH("user/comment/{id}")
    suspend fun editComment(
        @Path("id") commentId: String,
        @Body requestBody: UserEditCommentRequestEntity
    ): BaseApiResult<Any?>

    @DELETE("user/comment/{id}")
    suspend fun deleteComment(
        @Path("id") commentId: String
    ): BaseApiResult<Any?>

    @POST("user/favorites/delete")
    suspend fun deleteFavorite(
        @Body requestBody: DeleteFavoriteRequestEntity
    ): BaseApiResult<Any?>
}