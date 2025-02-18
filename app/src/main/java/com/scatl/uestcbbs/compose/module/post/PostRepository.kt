package com.scatl.uestcbbs.compose.module.post

import com.scatl.uestcbbs.compose.api.entity.request.CommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.DeleteFavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.FavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.PostReportRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.RateRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.VoteRequestEntity
import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.net.BaseApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/15 10:05:05
 */
class PostRepository @Inject constructor(): BaseRepository() {

    suspend fun getThreadDetail(
        threadId: String,
        authorId: String? = null,
        order: String,
        page: Int
    ) = postService.getThreadDetail(threadId, authorId, order, page)

    suspend fun getCommentAndRate(
        threadId: String,
        commentPids: String,
        ratePids: String,
        page: Int
    ) = postService.getCommentAndRate(threadId, commentPids, ratePids, page)

    suspend fun findThreadId(pid: String) = postService.findThreadId(pid)

    suspend fun vote(requestEntity: VoteRequestEntity) = postService.vote(requestEntity)

    suspend fun support(
        tid: String,
        pid: String?,
        support: Boolean
    ) = postService.support(tid, pid, support)

    suspend fun beforeUseRegretMagic(id: String?) = legacyService.beforeUseRegretMagic(id)

    suspend fun confirmUseRegretMagic(pid: String?, tid: String?): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["magicid"] = "20".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["operation"] = "use".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["usesubmit"] = "yes".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["handlekey"] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["idtype"] = "pid".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["pid"] = pid.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["ptid"] = tid.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["id"] = "$pid:$tid".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.confirmUseRegretMagic(map)
    }

    suspend fun comment(postId: Int, threadId: Int, message: String): BaseApiResult<Any?> {
        val body = CommentRequestEntity(
            threadId = threadId,
            postId = postId,
            message = message
        )
        return postService.comment(body)
    }

    suspend fun reply(requestEntity: CreatePostRequestEntity) = postService.reply(requestEntity)

    suspend fun favorite(
        requestEntity: FavoriteRequestEntity,
        tid: String
    ) = postService.favorite(requestEntity, tid)

    suspend fun deleteFavorite(
        requestEntity: DeleteFavoriteRequestEntity,
    ) = userService.deleteFavorite(requestEntity)

    suspend fun getFavoriteStatus(tid: String) = postService.getFavoriteStatus(tid)

    suspend fun getRateOptions(tid: String) = postService.getRateOptions(tid)

    suspend fun rate(pid: String, requestEntity: RateRequestEntity) = postService.rate(pid, requestEntity)

    suspend fun report(requestEntity: PostReportRequestEntity) = postService.report(requestEntity)
}