package com.scatl.uestcbbs.compose.api.entity.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageSummaryEntity(
    @Json(name = "new_chats")
    val newChats: List<NewChatEntity>? = listOf(),
    @Json(name = "new_messages")
    val newMessages: NewMessageEntity? = NewMessageEntity(),
    @Json(name = "new_notifications")
    val newNotifications: List<NewNotificationEntity>? = listOf()
)