package com.scatl.uestcbbs.compose.di.module

import android.content.Context
import androidx.room.Room
import com.scatl.uestcbbs.compose.BuildConfig
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.db.dao.AccountDao
import com.scatl.uestcbbs.compose.db.dao.BrowsingHistoryDao
import com.scatl.uestcbbs.compose.db.dao.FavoriteForumDao
import com.scatl.uestcbbs.compose.db.dao.MedalDao
import com.scatl.uestcbbs.compose.db.dao.SnapshotDao
import com.scatl.uestcbbs.compose.db.dao.VotedPostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * created by sca_tl at 2023/8/14 21:47
 */
@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDataBase {
        return Room
            .databaseBuilder(context, AppDataBase::class.java, "${BuildConfig.APPLICATION_ID}_database")
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideMedalDao(appDataBase: AppDataBase): MedalDao = appDataBase.getMedalDao()

    @Provides
    fun provideAccountDao(appDataBase: AppDataBase): AccountDao = appDataBase.getAccountDao()

    @Provides
    fun provideVotedPostDao(appDataBase: AppDataBase): VotedPostDao = appDataBase.getVotedPostDao()

    @Provides
    fun provideBrowsingHistoryDao(appDataBase: AppDataBase): BrowsingHistoryDao = appDataBase.getBrowsingHistoryDao()

    @Provides
    fun provideSnapshotDao(appDataBase: AppDataBase): SnapshotDao = appDataBase.getSnapshotDao()

    @Provides
    fun provideFavoriteForumDao(appDataBase: AppDataBase): FavoriteForumDao = appDataBase.getFavoriteForumDao()
}
