package com.scatl.uestcbbs.compose.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MessageManager {

    private val _unreadCount = MutableStateFlow(MessageUnreadEntity())
    val unreadCount: StateFlow<MessageUnreadEntity> = _unreadCount

    fun toggleUnread(entity: MessageUnreadEntity) {
        _unreadCount.value = entity
    }

}

data class MessageUnreadEntity(
    var totalUnreadCount: Int = 0,
    var pmUnreadCount: Int = 0,
    var replyUnreadCount: Int = 0,
    var atUnreadCount: Int = 0,
    var commentUnreadCount: Int = 0,

    var totalSysUnreadCount: Int = 0,
    var sysUnreadCount: Int = 0,
    var rateUnreadCount: Int = 0,
    var taskUnreadCount: Int = 0,
    var spaceUnreadCount: Int = 0,
    var friendUnreadCount: Int = 0
)