package com.scatl.uestcbbs.compose.module.wealth

import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2025/6/6 10:51:42
 */
class MyWealthRepository @Inject constructor(): BaseRepository() {

    suspend fun getCreditInfo(op: String) = legacyService.creditInfo(op)

}