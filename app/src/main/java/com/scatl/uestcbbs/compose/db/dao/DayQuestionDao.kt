package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.db.entity.MedalDBEntity
import com.scatl.uestcbbs.compose.db.entity.SnapshotDBEntity

/**
 * Created by sca_tl at 2024/9/25 16:13:12
 */
@Dao
interface DayQuestionDao: BaseDao<DayQuestionDBEntity> {

    @Query("select * from day_question")
    fun getAll(): List<DayQuestionDBEntity>

    @Query("select * from day_question where question = :question limit 1")
    fun findAnswer(question: String): DayQuestionDBEntity?
}