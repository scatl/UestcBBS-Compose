package com.scatl.uestcbbs.compose.module.message.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.module.message.MessageViewModel
import com.scatl.uestcbbs.compose.module.message.item.PrivateMsgItem
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/31 13:29:34
 */
@Composable
fun PrivateMsgListScreen() {
    val viewModel: MessageViewModel = hiltViewModel()
    val privateMsgData by viewModel.privateMsgData.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isScrollingUp by rememberIsScrollingUp(listState)

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(MessageService.MessageType.PRIVATE_MSG)) {
            viewModel.getPrivateMsgList(loadMore = false, init = true)
            viewModel.setPageInitialized(MessageService.MessageType.PRIVATE_MSG)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { isScrollingUp }.collect {
            SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(it))
        }
    }

    Column (
        modifier = Modifier
            .padding(horizontal = pagePadding)
            .padding(top = pagePadding)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        SwipeRefresh(
            uiState = privateMsgData,
            listState = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = pagePadding),
            onRefresh = {
                viewModel.getPrivateMsgList(loadMore = false, init = false)
            },
            onRetry = {
                viewModel.getPrivateMsgList(
                    loadMore = it == RetryType.LoadMore,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getPrivateMsgList(loadMore = true, init = false)
            }
        ) { index, item ->
            PrivateMsgItem(
                data = item
            )
        }
    }
}