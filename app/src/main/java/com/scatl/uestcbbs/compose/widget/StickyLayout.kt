package com.scatl.uestcbbs.compose.widget

import android.animation.ObjectAnimator
import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.ext.getScreenRefreshRate
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.px2dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * Created by sca_tl at 2024/5/22 16:14:33
 */
@Composable
fun StickyLayout(
    barContent: @Composable BoxScope.() -> Unit,
    headContent: @Composable BoxScope.() -> Unit,
    bodyContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0, to = 1.0) parallaxCoefficient: Float = 1f,
    bodyInitOffset: Int = 0,
    onSizeChanged: (barHeight: Float, headHeight: Float) -> Unit = { _, _ -> },
    onProgress: (percent: Float, offset: Float) -> Unit = { _, _ -> },
    onScrollDirectionChange: ((direction: Int?) -> Unit)? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    controller: State<StickyLayoutController> = rememberUpdatedState(remember { StickyLayoutController() }),
) {

    val tag = "StickyLayout"

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current

    var barContentHeight by rememberSaveable { mutableFloatStateOf(0f) }
    var headContentHeight by rememberSaveable { mutableFloatStateOf(0f) }
    val headContentScrollState = rememberScrollState()
    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }
//    val decay = remember { ExponentialDecay(friction = 0.075f * (60f / getScreenRefreshRate(context))) }
//    var flingJob by remember { mutableStateOf<Job?>(null) }
    var maxUpPx by rememberSaveable { mutableFloatStateOf(0f) }
    var offset by rememberSaveable { mutableFloatStateOf(0f) }
    var bodyScrollableState: ScrollableState? = null

    LaunchedEffect(headContentHeight) {
        maxUpPx = headContentHeight - barContentHeight - bodyInitOffset
    }

    LaunchedEffect(controller) {
        controller.value.scrollToTop = { duration ->
            scope.launchSafety { headContentScrollState.scrollTo(0) }
            ObjectAnimator
                .ofFloat(offset, 0f)
                .setDuration(duration ?: 500)
                .apply {
                    addUpdateListener {
                        offset = it.animatedValue as Float
                        onProgress(offset / -maxUpPx, offset)
                    }
                    start()
                }
        }

        controller.value.scrollToBody = { duration ->
            scope.launchSafety { headContentScrollState.scrollTo(0) }
            ObjectAnimator
                .ofFloat(offset, -maxUpPx)
                .setDuration(duration ?: 500)
                .apply {
                    addUpdateListener {
                        offset = it.animatedValue as Float
                        onProgress(offset / -maxUpPx, offset)
                    }
                    start()
                }
        }

        controller.value.headOffset = {
            offset
        }

        controller.value.bodyStateChange = {
            bodyScrollableState = it
        }

        controller.value.bodyState = {
            bodyScrollableState
        }
    }

    fun calculateOffset(delta: Float): Float {
        val oldOffset = offset
        val newOffset = (oldOffset + delta).coerceIn(-maxUpPx, 0f)
        offset = newOffset
        onProgress.invoke(offset / -maxUpPx, offset)
        return newOffset - oldOffset
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                XLog.tag(tag).d("offset:$offset, total:${offset + headContentHeight}, barHeight:${barContentHeight}, y:${available.y}")

                onScrollDirectionChange?.invoke(when {
                    available.y < 0 -> -1
                    available.y > 0 -> 1
                    else -> null
                })

                // 如果向上滚动，或者headContent已经有偏移（但未达到最大值）
                if (available.y < 0 && offset > -maxUpPx) {
                    XLog.tag(tag).d("1")
                    val o = calculateOffset(available.y)
                    return Offset(0f, o)
                }

                // 如果headContent偏移已经是最大值，且我们正在向上滚动，允许LazyColumn处理滚动事件
                if (available.y < 0 && offset == -maxUpPx) {
                    XLog.tag(tag).d("2")
                    scope.launchSafety { headContentScrollState.scrollTo(0) }
                    onProgress.invoke(1f, offset)
                    return Offset.Zero
                }

                // 如果headContent偏移已经是最大值，且我们正在向下滚动，允许LazyColumn处理滚动事件
                // 这里还需判断内容是否滑动到最顶部，滑动到最顶部再交由外层滑动
                if (available.y > 0 && offset == -maxUpPx) {
                    XLog.tag(tag).d("3")
                    if (bodyScrollableState?.canScrollBackward == true) {
                        XLog.tag(tag).d("9")
                        onProgress.invoke(1f, offset)
                        return Offset.Zero
                    }
                    val o = calculateOffset(available.y)
                    return Offset(0f, o)
                }

                // 如果headContent完全可见，且向下滚动，直接返回 Offset.Zero 不消费事件，允许 LazyColumn 滚动
                if (available.y > 0 && offset == 0f) {
                    XLog.tag(tag).d("4")
                    onProgress.invoke(0f, 0f)
                    return Offset.Zero
                }

                // 在其他情况下（向下滚动且headContent有偏移），消费滚动事件使headContent先滚动
                if (available.y > 0 && offset != 0f) {
                    XLog.tag(tag).d("5")
                    val o = calculateOffset(available.y)
                    return Offset(0f, o)
                }

                XLog.tag(tag).d("6")

                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // 内层滚动到顶了，将剩下的滚动量交给外层处理
                if (available.y > 0 && bodyScrollableState?.canScrollBackward == false && offset == -maxUpPx) {
                    val y = calculateOffset(available.y)
                    XLog.tag(tag).d("7, offset = $y")
                    return Offset(0f, y)
                }
                XLog.tag(tag).d("8")
                return Offset.Zero
            }

//            override suspend fun onPreFling(available: Velocity): Velocity {
//                return Velocity.Zero
//            }
//
//            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
//                XLog.tag(tag).d("onPostFling, available.y:${available.y}")
//                if (handleFling(available)) {
//                    fling(available)
//                    return available
//                }
//                return Velocity.Zero
//            }

//            private fun handleFling(available: Velocity): Boolean {
//                //手指从屏幕由上往下滑动
//                val condition1 = available.y > 0 && offset < 0f && bodyScrollableState?.canScrollBackward == false
//                //头部内容或者下面内容不能滑动时
//                val condition2 = headContentScrollState.canScrollForward.not() || bodyScrollableState?.canScrollForward?.not() == true
//                return condition1 || condition2
//            }
//
//            private fun fling(available: Velocity) {
//                flingJob?.cancel()
//                flingJob = scope.launchSafety {
//                    var remainingVelocity = available.y
//                    val frameDuration = (1000f / getScreenRefreshRate(context)).toLong()
//
//                    while (abs(remainingVelocity) > 0.5f) {
//                        // 计算位移（像素/秒 → 像素/帧）
//                        val delta = remainingVelocity * (frameDuration / 1000f)
//
//                        // 应用位移（自动处理边界）
//                        calculateOffset(delta)
//
//                        // 速度衰减
//                        remainingVelocity = decay.calculateTargetValue(remainingVelocity)
//
//                        // 精确帧延迟
//                        delay(frameDuration)
//
//                        // 如果到达边界，提前终止
//                        if (delta > 0f && offset >= 0f) break
//                        if (delta < 0f && offset <= -maxUpPx) break
//                    }
//                }
//            }
        }
    }

    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
//            .pointerInput(Unit) {
//                awaitPointerEventScope {
//                    while (true) {
//                        val event = awaitPointerEvent()
//                        if (event.changes.any { it.pressed }) {
//                            flingJob?.cancel()
//                        }
//                    }
//                }
//            }
            .nestedScroll(
                connection = nestedScrollConnection,
                dispatcher = nestedScrollDispatcher
            )
    ) {
        Box(
            modifier = Modifier
                .verticalScroll(headContentScrollState)
                .onSizeChanged {
                    headContentHeight = it.height.toFloat()
                    onSizeChanged.invoke(barContentHeight, headContentHeight)
                }
                .graphicsLayer {
                    translationY = offset * parallaxCoefficient
                },
            content = headContent,
        )

        Box(
            modifier = Modifier
                .padding(bottom = barContentHeight.px2dp)
                .offset {
                    IntOffset(
                        x = 0,
                        y = (offset + headContentHeight).toInt() - bodyInitOffset
                    )
                },
            content = bodyContent,
        )

        Box(
            modifier = Modifier
                .onSizeChanged {
                    barContentHeight = it.height.toFloat()
                    onSizeChanged.invoke(barContentHeight, headContentHeight)
                },
            content = barContent
        )
    }

}

class StickyLayoutController {
    var scrollToTop: ((duration: Long?) -> Unit)? = null
    var scrollToBody: ((duration: Long?) -> Unit)? = null
    var headOffset: (() -> Float)? = null
    var bodyState: (() -> ScrollableState?)? = null
    var bodyStateChange: ((state: ScrollableState) -> Unit)? = null
}

class ExponentialDecay(private val friction: Float = 0.015f) {
    fun calculateTargetValue(initialVelocity: Float): Float {
        val newVelocity = initialVelocity * (1f - friction)
        return newVelocity
    }
}