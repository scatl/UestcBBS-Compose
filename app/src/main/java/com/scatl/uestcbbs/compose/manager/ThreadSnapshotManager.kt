package com.scatl.uestcbbs.compose.manager

import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.util.FileUtil
import com.squareup.moshi.Moshi
import java.io.File

object ThreadSnapshotManager {

    private const val TAG = "ThreadSnapshotManager"

    fun saveSnapshot(data: ThreadDetailEntity?): Boolean {
        runCatching {
            val tid = data?.thread?.threadId ?: return false

            val jsonAdapter = Moshi.Builder().build().adapter(ThreadDetailEntity::class.java)
            val content = jsonAdapter.toJson(data)

            val fileName = System.currentTimeMillis().toString()
            val dir = File(getSnapshotDir(), tid.toString())
            if (!dir.exists()) {
                dir.mkdir()
            }
            return FileUtil.saveStringToFile(dir, fileName, content)
        }.onFailure {
            XLog.tag(TAG).d(it)
        }
        return false
    }

    fun getSnapshot(path: String?): ThreadDetailEntity? {
        if (path.isNullOrEmpty()) {
            return null
        }
        runCatching {
            val content = File(path).readText(Charsets.UTF_8)
            val jsonAdapter = Moshi.Builder().build().adapter(ThreadDetailEntity::class.java)
            val entity = jsonAdapter.fromJson(content)
            return entity
        }.onFailure {
            XLog.tag(TAG).d(it)
        }
        return null
    }

    fun getSnapshotDir(): File? {
        val dir = App.context.getExternalFilesDir("snapshots")
        if (dir?.exists() == false) {
            dir.mkdir()
        }
        return dir
    }

}