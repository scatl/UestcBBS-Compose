package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.SnapshotDBEntity

/**
 * Created by sca_tl at 2023/8/23 16:23
 */
@Dao
interface SnapshotDao: BaseDao<SnapshotDBEntity> {

    @Query("select * from snapshot where tid = :tid")
    fun findAllById(tid: String): List<SnapshotDBEntity>

    @Query("select * from snapshot where tid = :tid limit 1")
    fun findFirstById(tid: String): SnapshotDBEntity?

    @Query("delete from snapshot")
    fun deleteAll()
}