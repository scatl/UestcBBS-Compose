package com.scatl.uestcbbs.compose.widget.refresh

import androidx.annotation.IntRange
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.inspectable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.math.abs
import kotlin.math.pow

@Composable
fun <T: SwipeRefreshItem> SwipeRefresh(
    uiState: UiState<out List<T>>,
    modifier: Modifier = Modifier,
    refreshIndicator: RefreshIndicator = RefreshIndicator.Circle,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    enableRefresh: Boolean = true,
    enableLoadMore: Boolean = true,
    onRetry: (retryType: RetryType) -> Unit = {},
    @IntRange(from = 0) triggerLoadMoreOffset: Int = 5,
    listState: LazyListState = rememberLazyListState(),
    reverseLayout: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    verticalAlign: Alignment = Alignment.TopCenter,
    dataEmptyContentOffset: Dp = 0.dp,
    showNoMoreMsg: Boolean = true,
    showEmptyRetryBtn: Boolean = true,
    lottieResConfig: LottieResConfig = LottieResConfig(),
    key: (index: Int, item: T) -> Any? = { _, _ -> null },
    contentType: (index: Int, item: T) -> Any? = { _, _ -> null },
    itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) {
    val tag = "SwipeRefresh"

    val items = uiState.data
    val refreshing = uiState.isRefreshing
    val initState = uiState.initState
    val error = uiState.isError
    val success = uiState.isSuccess
    val hasMore = uiState.hasMore
    val loadingMore = uiState.isLoadingMore
    val errorData = uiState.errorData

    var oldItemsSize by remember { mutableIntStateOf(0) }
    var loadMoreKey by remember { mutableIntStateOf(0) }

    if (initState || refreshing) {
        oldItemsSize = 0
        loadMoreKey = 0
    }

    LaunchedEffect(loadMoreKey) {
        XLog.tag(tag).d(oldItemsSize.toString().plus(",").plus(loadMoreKey))
        if (items != null && loadMoreKey > oldItemsSize && enableLoadMore) {
            oldItemsSize = items.size
            onLoadMore()
        }
    }

    val state = rememberSwipeRefreshState(refreshing, onRefresh, refreshIndicator)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeRefresh(state, enableRefresh)
            .clipToBounds()
            .background(color = Color.Transparent)
            .graphicsLayer {
                translationY =
                    if (refreshIndicator == RefreshIndicator.Offset) state.position else 0f
            },
        contentAlignment = Alignment.TopCenter
    ) {
        if (enableRefresh && refreshIndicator == RefreshIndicator.Offset) {
            OffsetRefreshIndicator(
                refreshing = refreshing,
                lottieResConfig = lottieResConfig
            ) {
                state.position
            }
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier.align(verticalAlign),
                state = listState,
                reverseLayout = reverseLayout,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
            ) {
                if (items.isNullOrEmpty()) {
                    item {
                        Box (modifier = Modifier.fillParentMaxSize()) {
                            if (initState && !error) {
                                LoadingContent(
                                    modifier = Modifier.fillMaxSize(),
                                    lottieResConfig = lottieResConfig,
                                    dataEmptyContentOffset = dataEmptyContentOffset,
                                )
                            } else {
                                EmptyContent(
                                    modifier = Modifier.fillMaxSize(),
                                    error = error,
                                    errorData = errorData,
                                    showEmptyRetryBtn = showEmptyRetryBtn,
                                    dataEmptyContentOffset = dataEmptyContentOffset,
                                    lottieResConfig = lottieResConfig
                                ) {
                                    onRetry(RetryType.Init)
                                }
                            }
                        }
                    }
                } else {
                    items.forEachIndexed { index, item ->
                        if (item.isStickerHeader) {
                            stickyHeader(
                                key = key(index, item),
                                contentType = contentType(index, item)
                            ) {
                                itemContent(index, item)

                                if (hasMore && items.size - index < triggerLoadMoreOffset) {
                                    loadMoreKey = items.size
                                }
                            }
                        } else {
                            item(
                                key = key(index, item),
                                contentType = contentType(index, item)
                            ) {
                                itemContent(index, item)

                                if (hasMore && items.size - index < triggerLoadMoreOffset) {
                                    loadMoreKey = items.size
                                }
                            }
                        }
                    }

                    if (enableLoadMore) {
                        item {
                            MoreIndicator(
                                hasMore = hasMore,
                                loadingMore = loadingMore,
                                error = error,
                                showNoMoreMsg = showNoMoreMsg,
                                onRetry = onRetry,
                                lottieResConfig = lottieResConfig
                            )
                        }
                    }
                }

            }
        }

        if (enableRefresh && refreshIndicator == RefreshIndicator.Circle) {
            CircleRefreshIndicator(state, success, initState)
        }
    }
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    lottieResConfig: LottieResConfig,
    dataEmptyContentOffset: Dp = 0.dp,
) {
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieResConfig.loading))

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = lottieComposition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(150.dp)
                .offset(y = dataEmptyContentOffset)
        )
    }
}

@Composable
fun MoreIndicator(
    hasMore: Boolean,
    loadingMore: Boolean,
    error: Boolean,
    showNoMoreMsg: Boolean = true,
    lottieResConfig: LottieResConfig,
    onRetry: (retryType: RetryType) -> Unit = {},
) {
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieResConfig.loadMoreLoading))

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (error) {
                    onRetry(RetryType.LoadMore)
                }
            }
            .padding(vertical = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if ((hasMore || loadingMore) && !error) {
                LottieAnimation(
                    composition = lottieComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(20.dp, 20.dp)
                )
            }
            Text(
                text = if (error) {
                    stringResource(id = R.string.load_more_error)
                } else {
                    if (hasMore || loadingMore) {
                        stringResource(id = R.string.load_more_loading)
                    } else if (showNoMoreMsg) {
                        stringResource(id = R.string.load_more_no_more_data)
                    } else {
                        ""
                    }
                },
                fontSize = 14.sp,
                modifier = Modifier.alpha(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun OffsetRefreshIndicator(
    refreshing: Boolean,
    lottieResConfig: LottieResConfig,
    position: () -> Float
) {
    val loadingHeight = SwipeRefreshDefaults.RefreshingOffset
    val threadHeight = SwipeRefreshDefaults.RefreshThreshold
    val loadingHeightPx: Float
    val threadHeightPx: Float
    with(LocalDensity.current) {
        loadingHeightPx = loadingHeight.toPx()
        threadHeightPx = threadHeight.toPx()
    }

    var refreshHint by remember { mutableStateOf("") }
    var arrowRotated by remember { mutableStateOf(false) }
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieResConfig.arrowPullRefreshOffset))

    if (refreshing) {
        refreshHint = stringResource(R.string.refreshing)
    } else {
        if (position() > threadHeightPx) {
            arrowRotated = true
            refreshHint = stringResource(R.string.release_to_refresh)
        } else {
            arrowRotated = false
            refreshHint = stringResource(R.string.pull_to_refresh)
        }
    }

    val rotationDegree by animateFloatAsState(
        targetValue = if (arrowRotated) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "refresh_arrow_rotation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .graphicsLayer {
                translationY = (-position() - loadingHeightPx) * 0.45f
            }
            .height(loadingHeight)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer {
                    translationY = if (position() == 0f) -loadingHeightPx / 2 else 0f
                }
        ) {
            if (refreshing) {
                LottieAnimation(
                    composition = lottieComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(20.dp, 20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp, 25.dp)
                        .rotate(rotationDegree)
                )
            }
            Text(text = refreshHint, modifier = Modifier.offset(3.dp))
        }
    }
}

@Composable
fun CircleRefreshIndicator(
    state: SwipeRefreshState,
    success: Boolean,
    initState: Boolean
) {
    val scale = animateFloatAsState(
        targetValue = if (success && state.position == state.refreshOffset) 0f else 1f,
        animationSpec = tween(durationMillis = 400),
        label = "refresh_circle_scale"
    )

    if (!initState) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer {
                    translationY = state.position - 50.dp.toPx()
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .shadow(elevation = 5.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = CircleShape
                )
                .padding(10.dp)
        ) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(25.dp)
            )
        }
    }
}

@Composable
fun EmptyContent(
    modifier: Modifier = Modifier,
    error: Boolean,
    errorData: Throwable?,
    dataEmptyContentOffset: Dp = 0.dp,
    showEmptyRetryBtn: Boolean = true,
    lottieResConfig: LottieResConfig = LottieResConfig(),
    btnText: String = stringResource(id = R.string.refresh_init_try_again),
    onClick: () -> Unit = {}
) {
    val emptyLottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieResConfig.empty))
    val errorLottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieResConfig.error))
    val error404LottieComposition by rememberLottieComposition(LottieCompositionSpec.Asset(lottieResConfig.error404))

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .offset(y = dataEmptyContentOffset)
        ) {
            LottieAnimation(
                composition = if (error) {
                    if (errorData is HttpException && errorData.code() == 404) {
                        error404LottieComposition
                    } else {
                        errorLottieComposition
                    }
                } else {
                    emptyLottieComposition
                },
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.height(250.dp)
            )
            Text(
                text = if (error) {
                    if (errorData is HttpException && errorData.code() == 404) {
                        stringResource(R.string.loading_error_404)
                    } else {
                        if (errorData?.message.isNullOrEmpty()) {
                            stringResource(R.string.loading_error)
                        } else {
                            errorData?.message.toString()
                        }
                    }
                } else {
                    stringResource(R.string.refresh_init_empty)
                },
                modifier = Modifier.padding(horizontal = 40.dp),
                textAlign = TextAlign.Center,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            if (error && showEmptyRetryBtn) {
                Button(
                    onClick = onClick,
                ) {
                    Text(text = btnText)
                }
            }
        }
    }
}

@Composable
fun rememberSwipeRefreshState(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: RefreshIndicator = RefreshIndicator.Offset,
    refreshThreshold: Dp = SwipeRefreshDefaults.RefreshThreshold,
    refreshingOffset: Dp = SwipeRefreshDefaults.RefreshingOffset,
): SwipeRefreshState {
    require(refreshThreshold > 0.dp) { "The refresh trigger must be greater than zero!" }

    val scope = rememberCoroutineScope()
    val onRefreshState = rememberUpdatedState(onRefresh)
    val thresholdPx: Float
    val refreshingOffsetPx: Float

    with(LocalDensity.current) {
        thresholdPx = refreshThreshold.toPx()
        refreshingOffsetPx = refreshingOffset.toPx()
    }

    val state = remember(scope) {
        SwipeRefreshState(
            animationScope = scope,
            onRefreshState = onRefreshState,
            refreshIndicator = refreshIndicator,
            refreshingOffset = refreshingOffsetPx,
            threshold = thresholdPx
        )
    }

    SideEffect {
        state.setRefreshing(refreshing)
        state.setThreshold(thresholdPx)
        state.setRefreshingOffset(refreshingOffsetPx)
    }

    return state
}

fun Modifier.swipeRefresh(
    state: SwipeRefreshState,
    enabled: Boolean = true
) = inspectable(inspectorInfo = debugInspectorInfo {
    name = "swipeRefresh"
    properties["state"] = state
    properties["enabled"] = enabled
    properties["onPull"] = state::onPull
    properties["onRelease"] = state::onRelease
}) {
    Modifier.nestedScroll(
        SwipeRefreshNestedScrollConnection(
            state::onPull,
            state::onRelease,
            enabled
        )
    )
}

private class SwipeRefreshNestedScrollConnection(
    private val onPull: (pullDelta: Float) -> Float,
    private val onRelease: suspend (flingVelocity: Float) -> Float,
    private val enabled: Boolean
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = when {
        !enabled -> {
            Offset.Zero
        }
        source == NestedScrollSource.UserInput && available.y < 0 -> {
            Offset(0f, onPull(available.y))
        } // Swiping up
        else -> {
            Offset.Zero
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset = when {
        !enabled -> {
            Offset.Zero
        }
        source == NestedScrollSource.UserInput && available.y > 0 -> {
            Offset(0f, onPull(available.y))
        } // Pulling down
        else -> {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        return Velocity(0f, onRelease(available.y))
    }
}

class SwipeRefreshState internal constructor(
    private val animationScope: CoroutineScope,
    private val onRefreshState: State<() -> Unit>,
    private val refreshIndicator: RefreshIndicator = RefreshIndicator.Offset,
    refreshingOffset: Float,
    threshold: Float
) {

    private val progress get() = adjustedDistancePulled / threshold

    val refreshing get() = _refreshing
    val position get() = _position
    val refreshOffset get() = _refreshingOffset
    private val threshold get() = _threshold

    private val adjustedDistancePulled by derivedStateOf { distancePulled * 0.5f }

    private var _refreshing by mutableStateOf(false)

    private var _position by mutableFloatStateOf(0f)
    private var distancePulled by mutableFloatStateOf(0f)
    private var _threshold by mutableFloatStateOf(threshold)
    private var _refreshingOffset by mutableFloatStateOf(refreshingOffset)

    internal fun onPull(pullDelta: Float): Float {
        if (_refreshing) return 0f // Already refreshing, do nothing.

        val newOffset = (distancePulled + pullDelta).coerceAtLeast(0f)
        val dragConsumed = newOffset - distancePulled
        distancePulled = newOffset
        _position = calculateIndicatorPosition()
        return dragConsumed
    }

    internal fun onRelease(velocity: Float): Float {
        if (refreshing) return 0f // Already refreshing, do nothing

        if (adjustedDistancePulled > threshold) {
            onRefreshState.value()
        }

        animateIndicatorTo(0f)

        val consumed = when {
            // We are flinging without having dragged the pull refresh (for example a fling inside
            // a list) - don't consume
            distancePulled == 0f -> 0f
            // If the velocity is negative, the fling is upwards, and we don't want to prevent the
            // the list from scrolling
            velocity < 0f -> 0f
            // We are showing the indicator, and the fling is downwards - consume everything
            else -> velocity
        }
        distancePulled = 0f
        return consumed
    }

    internal fun setRefreshing(refreshing: Boolean) {
        if (_refreshing != refreshing) {
            _refreshing = refreshing
            distancePulled = 0f

            if (refreshing) {
                animateIndicatorTo(_refreshingOffset)
            } else {
                when(refreshIndicator) {
                    RefreshIndicator.Offset -> {
                        animateIndicatorTo(0f)
                    }
                    RefreshIndicator.Circle -> {
                        animateIndicatorTo(_refreshingOffset)
                    }
                }
            }
        }
    }

    internal fun setThreshold(threshold: Float) {
        _threshold = threshold
    }

    internal fun setRefreshingOffset(refreshingOffset: Float) {
        if (_refreshingOffset != refreshingOffset) {
            _refreshingOffset = refreshingOffset
            if (refreshing) animateIndicatorTo(refreshingOffset)
        }
    }

    // Make sure to cancel any existing animations when we launch a new one. We use this instead of
    // Animatable as calling snapTo() on every drag delta has a one frame delay, and some extra
    // overhead of running through the animation pipeline instead of directly mutating the state.
    private val mutatorMutex = MutatorMutex()

    private fun animateIndicatorTo(offset: Float) = animationScope.launch {
        mutatorMutex.mutate {
            animate(initialValue = _position, targetValue = offset) { value, _ ->
                _position = value
            }
        }
    }

    private fun calculateIndicatorPosition(): Float = when {
        // If drag hasn't gone past the threshold, the position is the adjustedDistancePulled.
        adjustedDistancePulled <= threshold -> adjustedDistancePulled
        else -> {
            // How far beyond the threshold pull has gone, as a percentage of the threshold.
            val overshootPercent = abs(progress) - 1.0f
            // Limit the overshoot to 200%. Linear between 0 and 200.
            val linearTension = overshootPercent.coerceIn(0f, 2f)
            // Non-linear tension. Increases with linearTension, but at a decreasing rate.
            val tensionPercent = linearTension - linearTension.pow(2) / 4
            // The additional offset beyond the threshold.
            val extraOffset = threshold * tensionPercent
            threshold + extraOffset
        }
    }
}

object SwipeRefreshDefaults {
    /**
     * If the indicator is below this threshold offset when it is released, a refresh
     * will be triggered.
     */
    val RefreshThreshold = 100.dp

    /**
     * The offset at which the indicator should be rendered whilst a refresh is occurring.
     */
    val RefreshingOffset = 100.dp
}

interface SwipeRefreshItem {
    var isStickerHeader: Boolean
}

sealed class RetryType {
    data object Init: RetryType()
    data object LoadMore: RetryType()
}

sealed class RefreshIndicator {
    data object Circle: RefreshIndicator()
    data object Offset: RefreshIndicator()
}

data class LottieResConfig(
    var loading: String = "lottie/loading.json",
    var arrowPullRefreshOffset: String = "lottie/pull_refreshing.json",
    var loadMoreLoading: String = "lottie/pull_refreshing.json",
    var empty: String = "lottie/empty.json",
    var error: String = "lottie/error.json",
    var error404: String = "lottie/error404.json"
)
