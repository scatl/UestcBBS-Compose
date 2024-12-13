package com.scatl.uestcbbs.compose.module.home

import com.scatl.uestcbbs.compose.api.service.TopListService.IdListType
import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/8/8 17:39:14
 */
class HomeRepository @Inject constructor(): BaseRepository() {

    suspend fun getTopList(idList: IdListType, page: Int) =
        topListService.getTopList(idList, page)

    suspend fun getBingDailyPic() = bingService.getBingDailyPic()

    suspend fun getNewThreadList(page: Int) = topListService.getTopList(IdListType.NEW_THREAD, page)

    suspend fun getIndexData() = indexService.getIndexData(
        globalStat = "1",
        announcement = "1",
        forumList = null,
        topList = null
    )
}