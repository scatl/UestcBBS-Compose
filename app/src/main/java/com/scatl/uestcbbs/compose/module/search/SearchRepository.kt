package com.scatl.uestcbbs.compose.module.search

import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/12 17:23:57
 */
class SearchRepository @Inject constructor(): BaseRepository() {

    suspend fun searchSummary(
        keyword: String
    ) = searchService.searchSummary(keyword)

    suspend fun searchThread(
        keyword: String,
        page: Int
    ) = searchService.searchThread(keyword, page)

    suspend fun searchUser(
        keyword: String,
        page: Int
    ) = searchService.searchUser(keyword, page)
}