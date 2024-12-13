package com.scatl.uestcbbs.compose.init

import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2023/8/24 10:53
 */
class InitRepository @Inject constructor() : BaseRepository() {

    suspend fun getIndexData() = indexService.getIndexData(
        globalStat = "1",
        announcement = "1",
        forumList = "1",
        topList = null
    )

    suspend fun getSystemSetting() = systemService.getSettings()

    suspend fun getMsgSummary() = messageService.getMessageSummary()
}