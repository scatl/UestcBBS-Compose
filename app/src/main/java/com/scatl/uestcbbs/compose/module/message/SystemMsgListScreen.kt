package com.scatl.uestcbbs.compose.module.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.MessageManager
import com.scatl.uestcbbs.compose.module.message.item.FriendItem
import com.scatl.uestcbbs.compose.module.message.item.RateItem
import com.scatl.uestcbbs.compose.module.message.item.SpaceItem
import com.scatl.uestcbbs.compose.module.message.item.SystemItem
import com.scatl.uestcbbs.compose.module.message.item.TaskItem
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/26 17:20:08
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SystemMsgListScreen() {
    val viewModel: MessageViewModel = hiltViewModel()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val messageType = rememberSaveable { mutableStateOf(MessageService.MessageType.SYSTEM) }
    val msgListData by viewModel.msgListData(messageType.value).collectAsStateWithLifecycle()
    val isScrollingUp by rememberIsScrollingUp(listState)

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(messageType.value)) {
            viewModel.getMsgListData(
                type = messageType.value,
                loadMore = false,
                init = true
            )
            viewModel.setPageInitialized(messageType.value)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { isScrollingUp }.collect {
            SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(it))
        }
    }

    val titles = remember { mutableStateListOf<Pair<MessageService.MessageType, String>>() }
    if (titles.isEmpty()) {
        titles.add(Pair(MessageService.MessageType.SYSTEM, stringResource(id = R.string.system_msg)))
        titles.add(Pair(MessageService.MessageType.RATE, stringResource(id = R.string.rate)))
        titles.add(Pair(MessageService.MessageType.TASK, stringResource(id = R.string.task)))
        titles.add(Pair(MessageService.MessageType.SPACE, stringResource(id = R.string.personal_space)))
        titles.add(Pair(MessageService.MessageType.FRIEND, stringResource(id = R.string.friend)))
    }

    Column {
        LazyRow (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = pagePadding),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(titles) { index, item ->
                ChipItem(
                    index = index,
                    item = item,
                    messageType = messageType,
                    viewModel = viewModel
                )
            }
        }

        SwipeRefresh(
            uiState = msgListData,
            listState = listState,
            onRefresh = {
                viewModel.getMsgListData(
                    type = messageType.value,
                    loadMore = false,
                    init = false
                )
            },
            onRetry = {
                viewModel.getMsgListData(
                    type = messageType.value,
                    loadMore = it != RetryType.Init,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getMsgListData(
                    type = messageType.value,
                    loadMore = true,
                    init = false
                )
            }
        ) { index, item ->
            when (messageType.value) {
                MessageService.MessageType.SYSTEM -> {
                    SystemItem(
                        modifier = Modifier.animateItem(),
                        data = item
                    )
                }

                MessageService.MessageType.RATE -> {
                    RateItem(
                        modifier = Modifier.animateItem(),
                        data = item
                    )
                }

                MessageService.MessageType.TASK -> {
                    TaskItem(
                        modifier = Modifier.animateItem(),
                        data = item
                    )
                }

                MessageService.MessageType.SPACE -> {
                    SpaceItem(
                        modifier = Modifier.animateItem(),
                        data = item
                    )
                }

                MessageService.MessageType.FRIEND -> {
                    FriendItem(
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

@Composable
private fun ChipItem(
    index: Int,
    item: Pair<MessageService. MessageType, String>,
    messageType: MutableState<MessageService.MessageType>,
    viewModel: MessageViewModel
) {
    val haptic = LocalHapticFeedback.current

    val unreadEntity by MessageManager.unreadCount.collectAsState()
    val unreadCounts = remember { mutableStateListOf(0, 0, 0, 0, 0) }

    LaunchedEffect(unreadEntity) {
        unreadCounts[0] = unreadEntity.sysUnreadCount
        unreadCounts[1] = unreadEntity.rateUnreadCount
        unreadCounts[2] = unreadEntity.taskUnreadCount
        unreadCounts[3] = unreadEntity.spaceUnreadCount
        unreadCounts[4] = unreadEntity.friendUnreadCount
    }

    FilterChip(
        label = {
            BadgedBox(
                badge = {
                    if (unreadCounts.getOrNull(index).toIntOrElse() > 0) {
                        Badge (
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-2).dp)
                        ) {
                            Text(
                                text = unreadCounts.getOrNull(index).toIntOrElse().toString(),
                                color = LocalCustomColors.current.unreadBadgeText
                            )
                        }
                    }
                }
            ) {
                Text(
                    text = item.second,
                    fontSize = 12.sp
                )
            }
        },
        shape = RoundedCornerShape(15.dp),
        border = null,
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            containerColor = MaterialTheme.colorScheme.surface
        ),
        selected = messageType.value == item.first,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            messageType.value = item.first
            viewModel.getMsgListData(
                type = messageType.value,
                loadMore = false,
                init = true
            )
        }
    )
}