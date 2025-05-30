package com.scatl.uestcbbs.compose.module.message

import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/25 22:38
 */
class MessageRepository @Inject constructor(): BaseRepository() {

    suspend fun getMessageList(
        kind: MessageService.MessageType,
        page: Int
    ) = messageService.getMessageList(kind, page)

    suspend fun getPrivateMsgList(
        page: Int
    ) = messageService.getPrivateMsgList(page)

    suspend fun getChatDetail(
        uid: Int,
        page: Int
    ) = messageService.getChatDetail(uid, page)

    suspend fun getUserSpace(
        id: String,
        `do`: String
    ) = legacyService.userSpace(id, `do`)
}