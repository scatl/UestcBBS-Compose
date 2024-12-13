package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.api.entity.ForumThreadsEntity
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/24 9:49:53
 */
interface ForumService {

    @GET("forum/details")
    suspend fun getIndexData(
        @Query("forum_id") forumId: Int
    ): BaseApiResult<ForumDetailEntity>

    @GET("thread/list")
    suspend fun getForumThreads(
        @Query("forum_id") forumId: String,
        @Query("type_id") typeId: Int? = null,
        @Query("sort_by") sortBy: String, //1: 最新回复  2: 最新发表  3: 最多回复  4: 最热主题
        @Query("forum_details") details: String,
        @Query("page") page: Int
    ): BaseApiResult<ForumThreadsEntity>
}