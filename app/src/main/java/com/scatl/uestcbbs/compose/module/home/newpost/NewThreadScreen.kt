package com.scatl.uestcbbs.compose.module.home.newpost

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.api.service.TopListService
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.dp2px
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.module.home.HomeViewModel
import com.scatl.uestcbbs.compose.module.home.newpost.entity.NewThreadData
import com.scatl.uestcbbs.compose.module.home.newpost.item.NewThreadBanner
import com.scatl.uestcbbs.compose.module.home.newpost.item.NewThreadSiteStatus
import com.scatl.uestcbbs.compose.module.home.toplist.ItemTopListPost
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/11 9:07:23
 */
@Composable
fun NewThreadScreen(
    topPadding: Dp,
    page: Int,
    pagerState: PagerState,
    onAlphaChanged: (Float) -> Unit,
    showRefreshBtn: MutableState<Boolean>,
) {
    val tag = "NewPostScreen"

    val viewModel: HomeViewModel = hiltViewModel()
    val newPostData by viewModel.newThreadData.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val topPaddingPx = topPadding.dp2px
    val isScrollingUp by rememberIsScrollingUp(listState)
    val context = LocalContext.current

    DisposableEffect(context) {
        val observer = Observer<Any> {
            if (pagerState.currentPage == page) {
                scope.launchSafety {
                    listState.scrollToItem(0)
                }
                viewModel.getNewThreadData(init = false, loadMore = false)
            }
        }

        val liveData = SharedFlowBus.on(Event.HOME_REFRESH)
        liveData.observe(context as LifecycleOwner, observer)

        onDispose {
            liveData.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(TopListService.IdListType.NEW_THREAD)) {
            viewModel.getNewThreadData(loadMore = false, init = true)
            viewModel.setPageInitialized(TopListService.IdListType.NEW_THREAD)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { isScrollingUp }.collect {
            SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(it))
        }
    }

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            showRefreshBtn.value = index > 10
        }
    }

    val visibility by remember {
        derivedStateOf {
            when {
                listState.layoutInfo.visibleItemsInfo.isNotEmpty()
                        && listState.firstVisibleItemIndex == 0
                        && !DataStore.hideDailyPic
                        && newPostData.data?.find { it.itemType == NewThreadData.NewThreadDataType.BANNER } != null -> {
                    val imageSize = listState.layoutInfo.visibleItemsInfo[0].size
                    val scrollOffset = listState.firstVisibleItemScrollOffset
                    scrollOffset / (imageSize.toFloat() - topPaddingPx)
                }
                else -> {
                    1f
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { visibility }.collect {
            onAlphaChanged.invoke(it)
        }
    }

    val firstItemTranslationY by remember {
        derivedStateOf {
            when {
                listState.layoutInfo.visibleItemsInfo.isNotEmpty() && listState.firstVisibleItemIndex == 0 -> {
                    listState.firstVisibleItemScrollOffset * 0.4f
                }
                else -> 0f
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(
                    top = if (DataStore.hideDailyPic ||
                        newPostData.data?.find { it.itemType == NewThreadData.NewThreadDataType.BANNER } == null
                    ) {
                        topPadding
                    } else {
                        0.dp
                    },
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            SwipeRefresh(
                uiState = newPostData,
                listState = listState,
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
                onRefresh = {
                    viewModel.getNewThreadData(init = false, loadMore = false)
                },
                onRetry = {
                    viewModel.getNewThreadData(
                        loadMore = it != RetryType.Init,
                        init = it == RetryType.Init
                    )
                },
                onLoadMore = {
                    viewModel.getNewThreadData(init = false, loadMore = true)
                },
                contentType = { _, item -> item.itemType }
            ) { _, item ->
                when (item.itemType) {
                    NewThreadData.NewThreadDataType.BANNER -> {
                        if (!DataStore.hideDailyPic
                            && newPostData.data?.find { it.itemType == NewThreadData.NewThreadDataType.BANNER } != null
                        ) {
                            NewThreadBanner(
                                data = (item as NewThreadData.Banner).data,
                                modifier = Modifier
                                    .graphicsLayer {
                                        alpha = 1f - visibility
                                        translationY = firstItemTranslationY
                                    }
                            )
                        }
                    }
                    NewThreadData.NewThreadDataType.SITE_STATUS -> {
                        NewThreadSiteStatus(
                            data = (item as NewThreadData.SiteStatus).data
                        )
                    }
                    NewThreadData.NewThreadDataType.NEW_THREAD -> {
                        ItemTopListPost(
                            data = (item as NewThreadData.NewThread).data
                        )
                    }
                }
            }
        }
    }
}