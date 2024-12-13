package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.message.ChatDetailEntity
import com.scatl.uestcbbs.compose.api.entity.message.MessageEntity
import com.scatl.uestcbbs.compose.api.entity.message.MessageSummaryEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/7/25 22:21
 */
interface MessageService {

    enum class MessageType(val type: String) {
        REPLY("reply"),
        COMMENT("comment"),
        AT("at"),
        RATE("rate"),

        SYSTEM("system"),
        TASK("task"),
//        REPORT("report"),
        SPACE("space"),
        FRIEND("friend"),

        PRIVATE_MSG("pm");

        override fun toString(): String {
            return type
        }
    }

    @GET("messages/notifications")
    suspend fun getMessageList(
        @Query("kind") kind: MessageType,
        @Query("page") page: Int
    ): BaseApiResult<MessageEntity>

    @GET("messages/summary")
    suspend fun getMessageSummary(): BaseApiResult<MessageSummaryEntity>

    @GET("messages/chat/list")
    suspend fun getPrivateMsgList(
        @Query("page") page: Int
    ): BaseApiResult<MessageEntity>

    @GET("messages/chat/user/{uid}")
    suspend fun getChatDetail(
        @Path("uid") uid: Int,
        @Query("page") page: Int
    ): BaseApiResult<ChatDetailEntity>
}