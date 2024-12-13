package com.scatl.uestcbbs.compose.eventbus

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

/**
 * SharedFlowBus 只会将更新通知给活跃的观察者，
 */
object SharedFlowBus {

    private val events = ConcurrentHashMap<String, MutableSharedFlow<Any>>()
    private val stickyEvents = ConcurrentHashMap<String, MutableSharedFlow<Any>>()

    // 创建或获取普通事件的 MutableSharedFlow
    fun with(key: String): MutableSharedFlow<Any> {
        return events.getOrPut(key) { MutableSharedFlow(extraBufferCapacity = 1) } as MutableSharedFlow<Any>
    }

    // 创建或获取粘性事件的 MutableSharedFlow
    fun withSticky(key: String): MutableSharedFlow<Any> {
        return stickyEvents.getOrPut(key) { MutableSharedFlow(replay = 1, extraBufferCapacity = 1) } as MutableSharedFlow<Any>
    }

    // 将普通事件转换为 LiveData
    fun on(key: String): LiveData<Any> {
        return with(key).asLiveData()
    }

    // 将粘性事件转换为 LiveData
    fun onSticky(key: String): LiveData<Any> {
        return withSticky(key).asLiveData()
    }

}