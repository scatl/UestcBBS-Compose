package com.scatl.uestcbbs.compose.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scatl.uestcbbs.compose.db.convert.StringListConvert
import com.scatl.uestcbbs.compose.db.dao.AccountDao
import com.scatl.uestcbbs.compose.db.dao.BrowsingHistoryDao
import com.scatl.uestcbbs.compose.db.dao.DayQuestionDao
import com.scatl.uestcbbs.compose.db.dao.FavoriteForumDao
import com.scatl.uestcbbs.compose.db.dao.SnapshotDao
import com.scatl.uestcbbs.compose.db.dao.MedalDao
import com.scatl.uestcbbs.compose.db.dao.VotedPostDao
import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.db.entity.FavoriteForumDBEntity
import com.scatl.uestcbbs.compose.db.entity.SnapshotDBEntity
import com.scatl.uestcbbs.compose.db.entity.MedalDBEntity
import com.scatl.uestcbbs.compose.db.entity.VotedPostDBEntity

/**
 * created by sca_tl at 2023/8/14 21:34
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = [
        SnapshotDBEntity::class,
        MedalDBEntity::class,
        AccountDBEntity::class,
        VotedPostDBEntity::class,
        BrowsingHistoryDBEntity::class,
        DayQuestionDBEntity::class,
        FavoriteForumDBEntity::class
    ]
)
@TypeConverters(StringListConvert::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getSnapshotDao(): SnapshotDao
    abstract fun getMedalDao(): MedalDao
    abstract fun getAccountDao(): AccountDao
    abstract fun getVotedPostDao(): VotedPostDao
    abstract fun getBrowsingHistoryDao(): BrowsingHistoryDao
    abstract fun getDayQuestionDao(): DayQuestionDao
    abstract fun getFavoriteForumDao(): FavoriteForumDao
}