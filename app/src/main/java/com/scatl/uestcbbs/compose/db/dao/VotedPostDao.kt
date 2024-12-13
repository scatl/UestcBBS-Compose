package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.VotedPostDBEntity

/**
 * Created by sca_tl at 2024/8/27 19:24:46
 */
@Dao
interface VotedPostDao: BaseDao<VotedPostDBEntity> {

    @Query("select * from vote_posts where pid = :pid")
    fun findAllById(pid: String): List<VotedPostDBEntity>

    fun findFirstById(pid: String) = findAllById(pid).getOrNull(0)
}