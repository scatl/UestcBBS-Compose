package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.search.SearchSummaryEntity
import com.scatl.uestcbbs.compose.api.entity.search.SearchThreadEntity
import com.scatl.uestcbbs.compose.api.entity.search.SearchUserEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/21 18:41
 */
interface SearchService {

    @GET("search/threads")
    suspend fun searchThread(
        @Query("q") keyword: String?,
        @Query("page") page: Int
    ): BaseApiResult<SearchThreadEntity>

    @GET("search/users")
    suspend fun searchUser(
        @Query("q") keyword: String?,
        @Query("page") page: Int
    ): BaseApiResult<SearchUserEntity>

    @GET("search/summary")
    suspend fun searchSummary(
        @Query("q") keyword: String
    ): BaseApiResult<SearchSummaryEntity>
}