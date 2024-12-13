package com.scatl.uestcbbs.compose.module.history

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/10 20:04:12
 */
class BrowsingHistoryRepository @Inject constructor(): BaseRepository() {

    fun getBrowsingHistory(start: Long?, end: Long?): SnapshotStateList<BrowsingHistoryDBEntity> {
        val result = SnapshotStateList<BrowsingHistoryDBEntity>()
        val data = if (start == null && end == null) {
            dataBase.getBrowsingHistoryDao().getAllDesc()
        } else if (start != null && end == null) {
            dataBase.getBrowsingHistoryDao().getAllByRangeDesc(start, start + Constants.DAY_MILLIS)
        } else if (start != null && end != null) {
            if (start == end) {
                dataBase.getBrowsingHistoryDao().getAllByRangeDesc(start, start + Constants.DAY_MILLIS)
            } else {
                dataBase.getBrowsingHistoryDao().getAllByRangeDesc(start, end)
            }
        } else {
            emptyList()
        }
        result.addAll(data)
        return result
    }

    fun deleteAll(start: Long?, end: Long?) {
        if (start == null && end == null) {
            dataBase.getBrowsingHistoryDao().deleteAll()
        } else if (start != null && end == null) {
            dataBase.getBrowsingHistoryDao().deleteByDateRange(start, start + Constants.DAY_MILLIS)
        } else if (start != null && end != null) {
            if (start == end) {
                dataBase.getBrowsingHistoryDao().deleteByDateRange(start, start + Constants.DAY_MILLIS)
            } else {
                dataBase.getBrowsingHistoryDao().deleteByDateRange(start, end)
            }
        }
    }

}