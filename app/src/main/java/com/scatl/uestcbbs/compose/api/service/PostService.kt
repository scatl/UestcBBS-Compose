package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.FavoriteStatusEntity
import com.scatl.uestcbbs.compose.api.entity.FindPostEntity
import com.scatl.uestcbbs.compose.api.entity.PostCommentAndRateEntity
import com.scatl.uestcbbs.compose.api.entity.RateOptionsEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadPollEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadReplyEntity
import com.scatl.uestcbbs.compose.api.entity.UploadAttachmentEntity
import com.scatl.uestcbbs.compose.api.entity.request.CommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.FavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.PostReportRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.RateRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.VoteRequestEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/15 10:07:16
 */
interface PostService {

    @GET("post/list?thread_details=1&forum_details=1")
    suspend fun getThreadDetail(
        @Query("thread_id") threadId: String,
        @Query("author_id") authorId: String? = null,
        @Query("order_type") order: String = "2", //1 ：最新在前  2：最新在后
        @Query("page") page: Int
    ): BaseApiResult<ThreadDetailEntity>

    @GET("post/details")
    suspend fun getCommentAndRate(
        @Query("thread_id") threadId: String,
        @Query("comment_pids") commentPid: String?, //点评pid 以 , 分割
        @Query("rate_pids") retePid: String?, //评分pid 以 , 分割
        @Query("page") page: Int
    ): BaseApiResult<Map<String, PostCommentAndRateEntity>>

    @GET("post/find")
    suspend fun findThreadId(
        @Query("pid") pid: String,
    ): BaseApiResult<FindPostEntity>

    @POST("thread/poll/vote")
    suspend fun vote(
        @Body requestBody: VoteRequestEntity
    ): BaseApiResult<ThreadPollEntity>

    @POST("post/vote")
    suspend fun support(
        @Query("tid") tid: String,
        @Query("pid") pid: String?,
        @Query("support") support: Boolean?
    ): BaseApiResult<Boolean?>

    @POST("post/review")
    suspend fun supportThread(
        @Query("tid") tid: String,
        @Query("support") support: Boolean?
    ): BaseApiResult<Int?>

    //点评
    @POST("post/comment")
    suspend fun comment(
        @Body requestBody: CommentRequestEntity
    ): BaseApiResult<Any?>

    @POST("thread/reply")
    suspend fun reply(
        @Body requestBody: CreatePostRequestEntity
    ): BaseApiResult<ThreadReplyEntity>

    @POST("attachment/upload")
    suspend fun uploadAttachment(
        @PartMap map: MutableMap<String, RequestBody> //kind   type  files[]
    ): BaseApiResult<UploadAttachmentEntity>

    @PUT("thread/{tid}/favorite")
    suspend fun favorite(
        @Body requestBody: FavoriteRequestEntity,
        @Path("tid") tid: String?
    ): BaseApiResult<Any>

    @GET("thread/{tid}/favorite")
    suspend fun getFavoriteStatus(
        @Path("tid") tid: String?
    ): BaseApiResult<FavoriteStatusEntity>

    @GET("post/{pid}/rate")
    suspend fun getRateOptions(
        @Path("pid") tid: String?
    ): BaseApiResult<RateOptionsEntity>

    @POST("post/{pid}/rate")
    suspend fun rate(
        @Path("pid") tid: String?,
        @Body requestBody: RateRequestEntity,
    ): BaseApiResult<Any>

    @POST("post/report")
    suspend fun report(
        @Body requestBody: PostReportRequestEntity,
    ): BaseApiResult<Any>
}