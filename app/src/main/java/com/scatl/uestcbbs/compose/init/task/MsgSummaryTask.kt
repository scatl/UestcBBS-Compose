package com.scatl.uestcbbs.compose.init.task

import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.init.InitRepository
import com.scatl.uestcbbs.compose.manager.MessageManager
import com.scatl.uestcbbs.compose.manager.MessageUnreadEntity
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/8/20 14:18:11
 */
class MsgSummaryTask @Inject constructor(
    val repository: InitRepository
): Task() {

    companion object {
        const val TAG = "MsgSummaryTask"
    }

    override fun execute() {
        job = coroutineScope.launchSafety {
            while (true) {
                repository
                    .getMsgSummary()
                    .onSuccess {
                        val chatCount = it?.newMessages?.chat.toIntOrElse()
                        val replyCount = it?.newMessages?.posts?.reply.toIntOrElse()
                        val commentCount = it?.newMessages?.posts?.comment.toIntOrElse()
                        val atCount = it?.newMessages?.posts?.at.toIntOrElse()

                        val sysCount = it?.newMessages?.system?.system.toIntOrElse()
                        val rateCount = it?.newMessages?.posts?.rate.toIntOrElse()
                        val taskCount = it?.newMessages?.system?.task.toIntOrElse()
                        val spaceCount = it?.newMessages?.system?.space.toIntOrElse()
                        val friendCount = it?.newMessages?.system?.friend.toIntOrElse()

                        val totalUnreadCount = chatCount + replyCount + commentCount + atCount +
                                sysCount + rateCount + taskCount + spaceCount + friendCount

                        val entity = MessageUnreadEntity(
                            totalUnreadCount = totalUnreadCount,
                            pmUnreadCount = chatCount,
                            replyUnreadCount = replyCount,
                            atUnreadCount = atCount,
                            commentUnreadCount = commentCount,

                            totalSysUnreadCount = sysCount + rateCount + taskCount + spaceCount + friendCount,
                            sysUnreadCount = sysCount,
                            rateUnreadCount = rateCount,
                            taskUnreadCount = taskCount,
                            spaceUnreadCount = spaceCount,
                            friendUnreadCount = friendCount
                        )

                        MessageManager.toggleUnread(entity)
                    }
                    .onFailure {
                        XLog.tag(TAG).e(it)
                    }
                delay(40_000)
            }
        }.onCatch {
            XLog.tag(TAG).e(it)
        }
    }
}