package com.scatl.uestcbbs.compose.module.forum

import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/11 15:24:54
 */
class ForumRepository @Inject constructor(): BaseRepository() {

    suspend fun getForumListData() = indexService.getIndexData(
        globalStat = null,
        announcement = null,
        forumList = "1",
        topList = null
    )

    suspend fun getForumDetail(fid: Int) = formService.getIndexData(fid)

    suspend fun getForumThreads(
        forumId: String,
        typeId: Int?,
        sortBy: String,
        details: String,
        page: Int
    ) = formService.getForumThreads(forumId, typeId, sortBy, details, page)

}