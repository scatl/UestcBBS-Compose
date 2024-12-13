package com.scatl.uestcbbs.compose.ext

import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by sca_tl at 2023/8/4 14:46
 *
 * copy from [Source](https://juejin.cn/post/7052269576851030030).
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <Result> CoroutineScope.launchSafety(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Result,
): SafetyCoroutine<Result> {
    val newContext = newCoroutineContext(context)
    val coroutine = SafetyCoroutine<Result>(newContext)
    coroutine.start(start, coroutine, block)
    return coroutine
}

@OptIn(InternalCoroutinesApi::class)
class SafetyCoroutine<T>(
    parentContext: CoroutineContext
) : AbstractCoroutine<T>(parentContext + CoroutineExceptionHandler {  _, error ->
    error.printStackTrace()
}, initParentJob = true, active = true) {

    private var catchBlock = ArrayList<((Throwable) -> Unit)>(0)

    private var successBlock = ArrayList<((T) -> Unit)>(0)

    private var cancelBlock = ArrayList<((Throwable) -> Unit)>(0)

    private var completeBlock = ArrayList<((T?) -> Unit)>(0)

    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        if (exception !is CancellationException) { // CancellationException 的不处理
            catchBlock.forEach { it.invoke(exception) }
        }
        return true
    }

    override fun onCompleted(value: T) {
        super.onCompleted(value)
        successBlock.forEach { it.invoke(value) }
        completeBlock.forEach { it.invoke(value) }
        removeCallbacks()
    }

    override fun onCancelled(cause: Throwable, handled: Boolean) {
        super.onCancelled(cause, handled)
        cancelBlock.forEach { it.invoke(cause) }
        completeBlock.forEach { it.invoke(null) }
        removeCallbacks()
    }

    private fun removeCallbacks() {
        successBlock.clear()
        catchBlock.clear()
        cancelBlock.clear()
        completeBlock.clear()
    }

    fun onCatch(catch: (e: Throwable) -> Unit) = apply {
        catchBlock.add(catch)
    }

    fun onSuccess(success: (T) -> Unit) = apply {
        successBlock.add(success)
    }

    fun onCancel(cancel: (Throwable) -> Unit) = apply {
        cancelBlock.add(cancel)
    }

    fun onComplete(complete: (T?) -> Unit) = apply {
        completeBlock.add(complete)
    }
}

