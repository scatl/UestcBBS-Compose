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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.px2dp

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
    var barContentHeight by rememberSaveable { mutableFloatStateOf(0f) }
    var headContentHeight by rememberSaveable { mutableFloatStateOf(0f) }
    val headContentScrollState = rememberScrollState()
    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }

    var maxUpPx by rememberSaveable { mutableFloatStateOf(0f) }
    LaunchedEffect(headContentHeight) {
        maxUpPx = headContentHeight - barContentHeight - bodyInitOffset
    }
    var offset by rememberSaveable { mutableFloatStateOf(0f) }

    var bodyScrollableState: ScrollableState? = null

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
        return newOffset - oldOffset
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                XLog.tag(tag).d("offset:$offset, total:${offset + headContentHeight}, barHeight:${barContentHeight}")

                onScrollDirectionChange?.invoke(when {
                    available.y < 0 -> -1
                    available.y > 0 -> 1
                    else -> null
                })

                // 如果向上滚动，或者headContent已经有偏移（但未达到最大值）
                if (available.y < 0 && offset > -maxUpPx) {
                    val o = Offset(0f, calculateOffset(available.y))
                    onProgress.invoke(offset / -maxUpPx, offset)
                    return o
                }

                // 如果headContent偏移已经是最大值，且我们正在向上滚动，允许LazyColumn处理滚动事件
                if (available.y < 0 && offset == -maxUpPx) {
                    scope.launchSafety { headContentScrollState.scrollTo(0) }
                    onProgress.invoke(1f, offset)
                    return Offset.Zero
                }

                // 如果headContent偏移已经是最大值，且我们正在向下滚动，允许LazyColumn处理滚动事件
                // 这里还需判断内容是否滑动到最顶部，滑动到最顶部再交由外层滑动
                if (available.y > 0 && offset == -maxUpPx) {
                    if (bodyScrollableState?.canScrollBackward == true) {
                        onProgress.invoke(1f, offset)
                        return Offset.Zero
                    }
                    val o = calculateOffset(available.y)
                    onProgress.invoke(offset / -maxUpPx, offset)
                    return Offset(0f, o)
                }

                // 如果headContent完全可见，且向下滚动，直接返回 Offset.Zero 不消费事件，允许 LazyColumn 滚动
                if (available.y > 0 && offset == 0f) {
                    onProgress.invoke(0f, 0f)
                    return Offset.Zero
                }

                // 在其他情况下（向下滚动且headContent有偏移），消费滚动事件使headContent先滚动
                if (available.y > 0 && offset != 0f) {
                    val o = calculateOffset(available.y)
                    onProgress.invoke(offset / -maxUpPx, offset)
                    return Offset(0f, o)
                }

                // 在其他情况下，不消费滚动事件
                onProgress.invoke(offset / -maxUpPx, offset)
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // 内层滚动到顶了，将剩下的滚动量交给外层处理
                if (available.y > 0 && bodyScrollableState?.canScrollBackward == false && offset == -maxUpPx) {
                    return Offset(0f, calculateOffset(available.y))
                }

                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection, nestedScrollDispatcher),
        contentAlignment = contentAlignment
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