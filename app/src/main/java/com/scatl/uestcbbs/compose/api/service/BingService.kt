package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.BingDailyPicEntity
import retrofit2.http.GET

/**
 * Created by sca_tl at 2024/7/11 9:21:10
 */
interface BingService {
    @GET("HPImageArchive.aspx?format=js&idx=0&n=5")
    suspend fun getBingDailyPic(): BingDailyPicEntity
}