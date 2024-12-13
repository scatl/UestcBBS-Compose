package com.scatl.uestcbbs.compose.module.forum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.module.forum.entity.ForumCategoryData
import com.scatl.uestcbbs.compose.module.forum.item.ForumCategoryChild
import com.scatl.uestcbbs.compose.module.forum.item.ForumCategoryTitle
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/11 15:23:46
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumCategoryScreen() {
    val viewModel: ForumViewModel = hiltViewModel()
    val navHostController = LocalNavController.current
    val forumData by viewModel.forumCategoryData.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isScrollingUp by rememberIsScrollingUp(listState)

    LoadInitialDataIfNeeded(key = forumData) {
        viewModel.getForumCategory(init = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { isScrollingUp }.collect {
            SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(it))
        }
    }

    Column (
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .navigationBarsPadding()
    ) {
        MediumTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.forum_categories),
                    fontSize = 25.sp
                )
            },
            scrollBehavior = scrollBehavior
        )

        SwipeRefresh(
            uiState = forumData,
            listState = listState,
            enableLoadMore = false,
            onRefresh = {
                viewModel.getForumCategory()
            },
            onRetry = {
                when(it) {
                    is RetryType.Init -> {
                        viewModel.getForumCategory(true)
                    }
                    is RetryType.LoadMore -> { }
                }
            },
            contentType = { _, item -> item.itemType }
        ) { idx, item ->
            when (item) {
                is ForumCategoryData.Title -> {
                    ForumCategoryTitle(
                        data = item.data
                    )
                }
                is ForumCategoryData.Children -> {
                    ForumCategoryChild(
                        data = item.data
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
//                        text = stringResource(R.string.forum_categories),
//                        fontSize = 25.sp
//                    )
//                },
//                scrollBehavior = scrollBehavior
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(
//                    top = paddingValues.calculateTopPadding(),
//                    bottom = paddingValues.calculateBottomPadding() + 10.dp
//                ),
//        ) {
//            SwipeRefresh(
//                uiState = forumData,
//                listState = listState,
//                enableLoadMore = false,
//                refreshIndicator = RefreshIndicator.Circle,
//                onRefresh = {
//                    viewModel.getForumCategory()
//                },
//                onRetry = {
//                    when(it) {
//                        is RetryType.Init -> {
//                            viewModel.getForumCategory(true)
//                        }
//                        is RetryType.LoadMore -> { }
//                    }
//                },
//                contentType = { _, item -> item.itemType }
//            ) { idx, item ->
//                when (item.itemType) {
//                    ForumCategoryData.ForumCategoryDataType.TITLE -> {
//                        ForumCategoryTitle(data = (item as ForumCategoryData.Title).data)
//                    }
//                    ForumCategoryData.ForumCategoryDataType.CHILDREN -> {
//                        ForumCategoryChild(
//                            data = (item as ForumCategoryData.Children).data,
//                            navHostController = navHostController
//                        )
//                    }
//                }
//            }
//        }
//    }
}