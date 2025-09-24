package com.scatl.uestcbbs.compose.module.home.toplist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.api.service.TopListService
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.module.home.HomeViewModel
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/11 9:07:33
 */
@Composable
fun TopListScreen(
    topPadding: Dp,
    page: Int,
    pagerState: PagerState,
    idListType: TopListService.IdListType,
    showRefreshBtn: MutableState<Boolean>,
) {
    val tag = "TopListScreen"

    val viewModel: HomeViewModel = hiltViewModel()
    val postData by viewModel.topListData(idListType).collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isScrollingUp by rememberIsScrollingUp(listState)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    DisposableEffect(context) {
        val observer = Observer<Any> {
            if (pagerState.currentPage == page) {
                scope.launchSafety {
                    listState.scrollToItem(0)
                }
                viewModel.getTopListData(
                    idList = idListType,
                    loadMore = false,
                    init = false
                )
            }
        }

        val liveData = SharedFlowBus.on(Event.HOME_REFRESH)
        liveData.observe(context as LifecycleOwner, observer)

        onDispose {
            liveData.removeObserver(observer)
        }
    }

    LaunchedEffect(idListType) {
        if (!viewModel.isPageInitialized(idListType)) {
            viewModel.getTopListData(
                idList = idListType,
                loadMore = false,
                init = true
            )
            viewModel.setPageInitialized(idListType)
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

    Scaffold { paddingValues ->
        Column (
            modifier = Modifier
                .padding(
                    top = topPadding + 0.dp,
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            SwipeRefresh(
                uiState = postData,
                listState = listState,
                onRefresh = {
                    viewModel.getTopListData(
                        idList = idListType,
                        loadMore = false,
                        init = false
                    )
                },
                onRetry = {
                    viewModel.getTopListData(
                        idList = idListType,
                        loadMore = it == RetryType.LoadMore,
                        init = it == RetryType.Init
                    )
                },
                onLoadMore = {
                    viewModel.getTopListData(
                        idList = idListType,
                        loadMore = true,
                        init = false
                    )
                }
            ) { index, item ->
                ItemTopListPost(
                    data = item
                )
            }
        }
    }
}