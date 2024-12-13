package com.scatl.uestcbbs.compose.module.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.module.message.item.AtItem
import com.scatl.uestcbbs.compose.module.message.item.CommentItem
import com.scatl.uestcbbs.compose.module.message.item.RateItem
import com.scatl.uestcbbs.compose.module.message.item.ReplyItem
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/25 22:21
 */
@Composable
fun MessageListScreen(
    messageType: MessageService.MessageType
) {
    val tag = "MessageListScreen"

    val viewModel: MessageViewModel = hiltViewModel()
    val msgListData by viewModel.msgListData(messageType).collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isScrollingUp by rememberIsScrollingUp(listState)

    LaunchedEffect(key1 = messageType) {
        if (!viewModel.isPageInitialized(messageType)) {
            viewModel.getMsgListData(
                type = messageType,
                loadMore = false,
                init = true
            )
            viewModel.setPageInitialized(messageType)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { isScrollingUp }.collect {
            SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(it))
        }
    }

    Column {
        SwipeRefresh(
            uiState = msgListData,
            listState = listState,
            onRefresh = {
                viewModel.getMsgListData(
                    type = messageType,
                    loadMore = false,
                    init = false
                )
            },
            onRetry = {
                viewModel.getMsgListData(
                    type = messageType,
                    loadMore = it != RetryType.Init,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getMsgListData(
                    type = messageType,
                    loadMore = true,
                    init = false
                )
            }
        ) { index, item ->
            when (messageType) {
                MessageService.MessageType.REPLY -> {
                    ReplyItem(
                        data = item
                    )
                }

                MessageService.MessageType.AT -> {
                    AtItem(
                        data = item
                    )
                }

                MessageService.MessageType.COMMENT -> {
                    CommentItem(
                        data = item
                    )
                }

                MessageService.MessageType.RATE -> {
                    RateItem(
                        modifier = Modifier.animateItem(),
                        data = item
                    )
                }

                else -> {

                }
            }
        }
    }
}