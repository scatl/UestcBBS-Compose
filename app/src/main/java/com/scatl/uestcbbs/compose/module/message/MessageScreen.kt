package com.scatl.uestcbbs.compose.module.message

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.manager.MessageManager
import com.scatl.uestcbbs.compose.module.message.chat.PrivateMsgListScreen
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.ScrollableTabLayout
import com.scatl.uestcbbs.compose.widget.TabIndicatorType
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/7/25 17:11:10
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MessageScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navHostController = LocalNavController.current
    val titles = remember { mutableStateListOf<String>() }
    if (titles.isEmpty()) {
        titles.add(stringResource(id = R.string.private_message_title))
        titles.add(stringResource(id = R.string.reply))
        titles.add(stringResource(id = R.string.at_me_title))
        titles.add(stringResource(id = R.string.comment))
        titles.add(stringResource(id = R.string.system))
    }

    val pagerState = rememberPagerState(
        pageCount = { titles.size }
    )

    Column (
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .navigationBarsPadding()
    ) {
        MediumTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.message_title),
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 20.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            scrollBehavior = scrollBehavior
        )

        Tab(
            titles = titles,
            pagerState = pagerState
        )

        HorizontalPager(
            state = pagerState,
            key = { index -> index }
        ) { page ->
            when (page) {
                0 -> {
                    PrivateMsgListScreen()
                }
                4 -> {
                    SystemMsgListScreen()
                }
                else -> {
                    MessageListScreen(
                        messageType = when(page) {
                            1 -> MessageService.MessageType.REPLY
                            2 -> MessageService.MessageType.AT
                            3 -> MessageService.MessageType.COMMENT
                            else -> MessageService.MessageType.RATE
                        }
                    )
                }
            }
        }
    }

//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            MediumTopAppBar(
//                title = {
//                    Text(
//                        text = stringResource(R.string.message_title),
//                        fontSize = 25.sp,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(end = 20.dp)
//                    )
//                },
//                colors = TopAppBarDefaults.mediumTopAppBarColors().copy(
//                    containerColor = MaterialTheme.colorScheme.surfaceContainer
//                ),
//                scrollBehavior = scrollBehavior
//            )
//        }
//    ) { paddingValues ->
//        Column (
//            modifier = Modifier
//                .fillMaxSize()
//                .graphicsLayer {
//                    //translationY = paddingValues.calculateTopPadding().toPx()
//                }
//                //.offset(y  = paddingValues.calculateTopPadding())
//                .padding(paddingValues)
//        ) {
//            ScrollableTabLayout(
//                tabs = titles,
//                indicatorType = TabIndicatorType.OVAL,
//                modifier = Modifier
//                    .background(color = Color.Red.copy(alpha = Random.nextDouble(0.0, 0.9).toFloat()))
//                    //.background(color = MaterialTheme.colorScheme.surfaceContainer)
//                    .padding(bottom = 10.dp),
//                selectTabStyle = TextStyle(
//                    color = Color.White,
//                    fontSize = 15.sp,
//                ),
//                unSelectTabStyle = TextStyle(
//                    color = MaterialTheme.colorScheme.onSurface,
//                    fontSize = 15.sp
//                ),
//                tabClick = {
//                    scope.launch {
//                        pagerState.animateScrollToPage(it)
//                    }
//                },
//                pagerState = pagerState
//            )
//
//            HorizontalPager(
//                state = pagerState,
//                key = { index -> index }
//            ) { page ->
//                when (page) {
//                    4 -> {
//                        SystemMsgListScreen(
//                            navHostController = navHostController
//                        )
//                    }
//                    else -> {
//                        MessageListScreen(
//                            navHostController = navHostController,
//                            messageType = when(page) {
//                                0 -> MessageService.MessageType.REPLY
//                                1 -> MessageService.MessageType.AT
//                                2 -> MessageService.MessageType.COMMENT
//                                else -> MessageService.MessageType.RATE
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
}

@Composable
private fun Tab(
    titles: List<String>,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()

    val unreadEntity by MessageManager.unreadCount.collectAsState()
    val unreadCounts = remember { mutableStateListOf(0, 0, 0, 0, 0) }
    LaunchedEffect(unreadEntity) {
        unreadCounts[0] = unreadEntity.pmUnreadCount
        unreadCounts[1] = unreadEntity.replyUnreadCount
        unreadCounts[2] = unreadEntity.atUnreadCount
        unreadCounts[3] = unreadEntity.commentUnreadCount
        unreadCounts[4] = unreadEntity.totalSysUnreadCount
    }

    ScrollableTabLayout(
        tabs = titles,
        dotCounts = unreadCounts,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        indicatorType = TabIndicatorType.OVAL,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(bottom = pagePadding, start = pagePadding)
            .padding(end = pagePadding),
        selectTabStyle = TextStyle(
            color = Color.White,
            fontSize = 15.sp,
        ),
        unSelectTabStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp
        ),
        tabClick = {
            scope.launch {
                pagerState.animateScrollToPage(it)
            }
        },
        pagerState = pagerState
    )
}