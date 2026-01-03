package com.scatl.uestcbbs.compose.manager

import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.module.home.entity.NewThreadCacheEntity
import com.scatl.uestcbbs.compose.util.FileUtil
import com.squareup.moshi.Moshi
import java.io.File

object CacheDataManager {

    fun saveNewThreadData(data: NewThreadCacheEntity) {
        runCatching {
            val jsonString = Moshi
                .Builder()
                .build()
                .adapter(NewThreadCacheEntity::class.java)
                .toJson(data)
            val dir = App.context.getExternalFilesDir(null)
            if (dir != null) {
                FileUtil.saveStringToFile(dir, "new_thread_cache", jsonString)
            }
        }
    }

    fun getNewThreadData(): NewThreadCacheEntity? {
        return try {
            val file = File(App.context.getExternalFilesDir(null), "new_thread_cache")
            val content = File(file.absolutePath).readText(Charsets.UTF_8)
            Moshi
                .Builder()
                .build()
                .adapter(NewThreadCacheEntity::class.java)
                .fromJson(content)
        } catch (_: Exception) {
            null
        }
    }

}