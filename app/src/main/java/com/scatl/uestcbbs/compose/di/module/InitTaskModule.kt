package com.scatl.uestcbbs.compose.di.module

import com.scatl.uestcbbs.compose.init.InitRepository
import com.scatl.uestcbbs.compose.init.task.BBSSettingsTask
import com.scatl.uestcbbs.compose.init.task.DataBaseTask
import com.scatl.uestcbbs.compose.init.task.GetIndexDataTask
import com.scatl.uestcbbs.compose.init.task.MsgSummaryTask
import com.scatl.uestcbbs.compose.init.task.TaskInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by sca_tl at 2024/8/20 15:35:01
 */
@Module
@InstallIn(SingletonComponent::class)
class InitTaskModule {

    @Singleton
    @Provides
    fun provideTaskInitializer(
        getIndexDataTask: GetIndexDataTask,
        bbsSettingsTask: BBSSettingsTask,
        msgSummaryTask: MsgSummaryTask,
        dataBaseTask: DataBaseTask
    ): TaskInitializer {
        return TaskInitializer(
            getIndexDataTask,
            bbsSettingsTask,
            msgSummaryTask,
            dataBaseTask
        )
    }

    @Provides
    fun provideGetIndexDataTask(
        initRepository: InitRepository
    ): GetIndexDataTask = GetIndexDataTask(initRepository)

    @Provides
    fun provideBBSSettingsTask(
        initRepository: InitRepository
    ): BBSSettingsTask = BBSSettingsTask(initRepository)

    @Provides
    fun provideMsgSummaryTask(
        initRepository: InitRepository
    ): MsgSummaryTask = MsgSummaryTask(initRepository)

    @Provides
    fun provideDataBaseTask(
        initRepository: InitRepository
    ): DataBaseTask = DataBaseTask(initRepository)
}