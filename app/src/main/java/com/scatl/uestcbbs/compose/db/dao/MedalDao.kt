package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.MedalDBEntity

/**
 * Created by sca_tl at 2024/7/15 22:30
 */
@Dao
interface MedalDao : BaseDao<MedalDBEntity> {

    @Query("select * from medals where id = :id")
    fun findAllById(id: Int): List<MedalDBEntity>

    fun findFirstById(id: Int) = findAllById(id).getOrNull(0)

    @Query("delete from medals")
    fun deleteAll()
}