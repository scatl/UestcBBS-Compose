package com.scatl.uestcbbs.compose.init.task

import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.init.InitRepository
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/5/27 19:42:09
 */
class GetIndexDataTask @Inject constructor(
    val repository: InitRepository
): Task() {

    companion object {
        const val TAG = "GetIndexDataTask"
    }

    override fun execute() {
        coroutineScope.launchSafety {
            repository
                .getIndexData()
                .onSuccess {
                    if (it != null && it.forumList.isNotNullAndEmpty()) {
                        ForumCategoryManager.initData(it.forumList)
                    }
                }
                .onFailure {

                }
        }.onCatch {
            XLog.tag(TAG).e(it)
        }
    }
}