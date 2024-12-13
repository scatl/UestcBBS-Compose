package com.scatl.uestcbbs.compose.module.watertask

import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/24 16:32:33
 */
class WaterTaskRepository @Inject constructor(): BaseRepository() {

    suspend fun getDoingTask() = legacyService.getDoingTask()

    suspend fun deleteDoingTask(id: Int) = legacyService.deleteDoingTask(id = id)

    suspend fun getNewTask() = legacyService.getNewTask()

    suspend fun applyNewTask(id: Int) = legacyService.applyNewTask(id = id)

    suspend fun getDoneTask() = legacyService.getDoneTask()

    suspend fun getTaskAward(id: Int) = legacyService.getTaskAward(id = id)

    suspend fun getFailedTask() = legacyService.getFailedTask()
}