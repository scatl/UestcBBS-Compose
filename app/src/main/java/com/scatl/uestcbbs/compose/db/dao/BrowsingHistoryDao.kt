package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity

/**
 * Created by sca_tl at 2024/9/10 19:36:31
 */
@Dao
interface BrowsingHistoryDao : BaseDao<BrowsingHistoryDBEntity> {

    @Query("select * from browsing_history where id = :id")
    fun findAllById(id: Int): List<BrowsingHistoryDBEntity>

    @Query("select * from browsing_history")
    fun getAll(): List<BrowsingHistoryDBEntity>

    @Query("select * from browsing_history order by lastBrowserDate desc")
    fun getAllDesc(): List<BrowsingHistoryDBEntity>

    @Query("select * from browsing_history where lastBrowserDate between :startDate and :endDate order by lastBrowserDate desc")
    fun getAllByRangeDesc(startDate: Long, endDate: Long): List<BrowsingHistoryDBEntity>

    fun findFirstById(id: Int) = findAllById(id).getOrNull(0)

    @Query("delete from browsing_history")
    fun deleteAll()

    @Query("delete from browsing_history where lastBrowserDate between :startDate and :endDate")
    fun deleteByDateRange(startDate: Long, endDate: Long): Int
}