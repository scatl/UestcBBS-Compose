package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.api.entity.TopListEntity
import com.scatl.uestcbbs.compose.api.service.TopListService.IdListType
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/11 13:34:19
 */
interface IndexService {

    @GET("index")
    suspend fun getIndexData(
        @Query ("global_stat") globalStat: String?,
        @Query ("announcement") announcement: String?,
        @Query ("forum_list") forumList: String?,
        @Query ("top_list") topList: String?
    ): BaseApiResult<IndexEntity>

}