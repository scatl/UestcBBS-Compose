package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.api.entity.SystemSettingEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/15 20:56:16
 */
interface SystemService {

    @GET("system/settings/all")
    suspend fun getSettings(): BaseApiResult<SystemSettingEntity>

}