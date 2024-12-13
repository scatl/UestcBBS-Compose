package com.scatl.uestcbbs.compose.module.forum.detail

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.forum.ForumViewModel
import com.scatl.uestcbbs.compose.module.forum.entity.ForumThreadsData
import com.scatl.uestcbbs.compose.module.forum.item.ForumThreadsFilterItem
import com.scatl.uestcbbs.compose.module.forum.item.ForumThreadsStickItem
import com.scatl.uestcbbs.compose.module.forum.item.ForumThreadsTitleItem
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadItem
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/24 17:21:31
 */
@Composable
fun ForumThreadsScreen(
    state: LazyListState,
    viewModel: ForumViewModel,
    fid: String
) {
    val forumThreadsData by viewModel.forumThreadsData(fid).collectAsStateWithLifecycle()
    val currentTypeId = rememberSaveable { mutableStateOf<Int?>(null) }
    val currentSortBy = rememberSaveable { mutableStateOf("1") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = fid) {
        if (!viewModel.isChildInitialized(fid)) {
            viewModel.getForumThreadsData(
                fid = fid,
                typeId = currentTypeId.value,
                sortBy = currentSortBy.value,
                loadMore = false,
                init = true
            )
            viewModel.setChildInitialized(fid)
        }
    }

    SwipeRefresh(
        uiState = forumThreadsData,
        listState = state,
        onRefresh = {
            viewModel.getForumThreadsData(
                fid = fid,
                typeId = currentTypeId.value,
                sortBy = currentSortBy.value,
                loadMore = false,
                init = false
            )
        },
        onRetry = {
            viewModel.getForumThreadsData(
                fid = fid,
                typeId = currentTypeId.value,
                sortBy = currentSortBy.value,
                loadMore = it == RetryType.LoadMore,
                init = it == RetryType.Init
            )
        },
        onLoadMore = {
            viewModel.getForumThreadsData(
                fid = fid,
                typeId = currentTypeId.value,
                sortBy = currentSortBy.value,
                loadMore = true,
                init = false
            )
        }
    ) { index, item ->
        when (item.itemType) {
            ForumThreadsData.ForumThreadsDataType.FILTER -> {
                ForumThreadsFilterItem(
                    data = (item as ForumThreadsData.Filter).data,
                    selectedTypeId = currentTypeId.value,
                    onTypeSelected = {
                       scope.launchSafety {
                           state.scrollToItem(0)
                       }
                        currentTypeId.value = it
                        viewModel.getForumThreadsData(
                            fid = fid,
                            typeId = currentTypeId.value,
                            sortBy = currentSortBy.value,
                            loadMore = false,
                            init = false
                        )
                    }
                )
            }

            ForumThreadsData.ForumThreadsDataType.STICK -> {
                ForumThreadsStickItem(
                    data = (item as ForumThreadsData.Stick).data
                )
            }

            ForumThreadsData.ForumThreadsDataType.THREAD_TITLE -> {
                ForumThreadsTitleItem(
                    onSortSelected = {
                        currentSortBy.value = it
                        viewModel.getForumThreadsData(
                            fid = fid,
                            typeId = currentTypeId.value,
                            sortBy = currentSortBy.value,
                            loadMore = false,
                            init = false
                        )
                    }
                )
            }

            ForumThreadsData.ForumThreadsDataType.THREAD -> {
                CommonThreadItem(
                    data = (item as ForumThreadsData.Thread).data,
                )
            }
        }
    }
}