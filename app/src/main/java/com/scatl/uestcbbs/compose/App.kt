package com.scatl.uestcbbs.compose

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.init.task.TaskInitializer
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.manager.EmotionManager
import com.scatl.uestcbbs.compose.module.download.DownLoadManager
import com.wy.lib.wytrace.ArtMethodTrace
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * created by sca_tl at 2023/7/19 22:01
 */
@HiltAndroidApp
@SuppressLint("StaticFieldLeak")
class App: Application(), ImageLoaderFactory {

    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var taskInitializer: TaskInitializer
    @Inject lateinit var dataBase: AppDataBase

    companion object {
        @JvmStatic lateinit var context: Context
    }

    init {
        XLog.init(LogLevel.ALL)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        AccountManager.toggleSigned(dataBase.getAccountDao().getSignedInAccount())
        DownLoadManager.initDownloadFolderUri(context)
        ArtMethodTrace.fix14debugApp(this)
        EmotionManager.init(context)
        taskInitializer.init()
    }

    override fun newImageLoader() = imageLoader
}