package com.scatl.uestcbbs.compose.api.service

import androidx.annotation.StringDef
import com.scatl.uestcbbs.compose.api.entity.TopListEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/11 9:01:17
 */
interface TopListService {

    enum class IdListType(val type: String) {
        NEW_THREAD("newthread"),
        NEW_REPLY("newreply"),
        DIGEST("digest"),
        LIFE("life"),
        HOT_LIST("hotlist");

        override fun toString() = type
    }

    @GET("forum/toplist")
    suspend fun getTopList(
        @Query("idlist") idList: IdListType,
        @Query("page") page: Int
    ): BaseApiResult<TopListEntity>
}