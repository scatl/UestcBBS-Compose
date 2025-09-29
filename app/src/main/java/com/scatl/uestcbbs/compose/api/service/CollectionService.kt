package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.request.FavoriteRequestEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by sca_tl at 2025/9/25 14:16:09
 */
interface CollectionService {

    @PUT("thread/{tid}/favorite")
    suspend fun addThreadToCollection(
        @Body requestBody: FavoriteRequestEntity,
        @Path("tid") tid: Int
    ): BaseApiResult<Any?>

}