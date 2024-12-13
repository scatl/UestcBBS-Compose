package com.scatl.uestcbbs.compose.module.snapshot

import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.db.entity.SnapshotDBEntity
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.ThreadSnapshotManager
import com.scatl.uestcbbs.compose.module.snapshot.entity.SnapshotData
import com.scatl.uestcbbs.compose.util.FileUtil
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/14 15:59:05
 */
class SnapshotRepository @Inject constructor(): BaseRepository() {

    fun saveSnapshot(data: ThreadDetailEntity): Boolean {
        val success = ThreadSnapshotManager.saveSnapshot(data)

        if (success) {
            val exist = dataBase.getSnapshotDao().findFirstById(data.thread?.threadId.toString())
            if (exist == null) {
                dataBase.getSnapshotDao().insert(
                    SnapshotDBEntity(
                        id = 0,
                        tid = data.thread?.threadId.toString(),
                        subject = data.thread?.subject.toString()
                    )
                )
            }
        }

        return success
    }

    fun getAllSnapshot(): List<SnapshotData> {
        val result = mutableListOf<SnapshotData>()
        val dir = ThreadSnapshotManager.getSnapshotDir()
        if (dir != null && dir.isDirectory) {
            dir.listFiles()?.sortedByDescending { f -> f.lastModified() }?.forEach {
                if (it.isDirectory && it.name.toIntOrElse() != 0) {
                    val snapshots = it.listFiles()?.sortedByDescending { f-> f.lastModified() }
                    if (!snapshots.isNullOrEmpty()) {
                        val data = SnapshotData(
                            tid = it.name,
                            subject = dataBase.getSnapshotDao().findFirstById(it.name)?.subject.toString(),
                            snapshots = mutableListOf()
                        )
                        result.add(data)
                        snapshots.forEach { snapshot ->
                            data.snapshots.add(snapshot.absolutePath)
                        }
                    }
                }
            }
        }
        return result
    }

    fun deleteAllSnapshot(): Boolean {
        val dir = ThreadSnapshotManager.getSnapshotDir()
        return FileUtil.deleteDir(dir, false)
    }
}