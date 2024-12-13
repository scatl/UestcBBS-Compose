package com.scatl.uestcbbs.compose.module.setting

import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.util.FileUtil
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/6/27 14:34:36
 */
class SettingRepository @Inject constructor(): BaseRepository() {

    fun getCacheSize(): String {
        return FileUtil.formatFileSize(
            FileUtil.getDirectorySize(App.context.cacheDir)
        )
    }

    fun deleteCache() {
        FileUtil.deleteDir(App.context.cacheDir, false)
    }

}