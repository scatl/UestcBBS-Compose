package com.scatl.uestcbbs.compose.module.user.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.module.user.UserProfilePage
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.widget.refresh.RefreshIndicator
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/9/3 13:48:19
 */
@Composable
fun UserFriendScreen(
    data: UserProfileEntity,
    viewModel: UserViewModel,
    state: LazyListState = rememberLazyListState()
) {
    val friendData by viewModel.friendData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(UserProfilePage.FRIEND)) {
            viewModel.getUserFriends(loadMore = false, init = true)
            viewModel.setPageInitialized(UserProfilePage.FRIEND)
        }
    }

    SwipeRefresh(
        uiState = friendData,
        listState = state,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer),
        onRefresh = {
            viewModel.getUserFriends(loadMore = false, init = false)
        },
        onRetry = {
            viewModel.getUserFriends(
                loadMore = it == RetryType.LoadMore,
                init = it == RetryType.Init
            )
        },
        onLoadMore = {
            viewModel.getUserFriends(loadMore = true, init = false)
        }
    ) { index, item ->
        UserFriendItem(
            modifier = Modifier.animateItem(),
            item = item,
            data = data,
            viewModel = viewModel,
            deleteCallback = { uid ->
                uid?.let {
                    friendData.data?.removeIf { it.uid.toString() == uid }
                }
            }
        )
    }
}