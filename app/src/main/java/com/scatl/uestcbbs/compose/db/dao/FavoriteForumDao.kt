package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.FavoriteForumDBEntity

/**
 * Created by sca_tl at 2024/11/20 16:51:07
 */
@Dao
interface FavoriteForumDao: BaseDao<FavoriteForumDBEntity>{

    @Query("select * from favorite_forum")
    fun getAll(): List<FavoriteForumDBEntity>

    @Query("delete from favorite_forum where id = :id")
    fun delete(id: Int)

    @Query("delete from favorite_forum")
    fun deleteAll()
}