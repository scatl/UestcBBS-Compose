package com.scatl.uestcbbs.compose.module.user.collect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadItem
import com.scatl.uestcbbs.compose.module.user.UserProfilePage
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.widget.refresh.RefreshIndicator
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/21 19:49
 */
@Composable
fun UserCollectionScreen(
    data: UserProfileEntity,
    viewModel: UserViewModel,
    state: LazyListState = rememberLazyListState()
) {
    val collectionData by viewModel.collectionData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(UserProfilePage.COLLECTION)) {
            viewModel.getUserCollection(loadMore = false, init = true)
            viewModel.setPageInitialized(UserProfilePage.COLLECTION)
        }
    }

    Column (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        SwipeRefresh(
            uiState = collectionData,
            listState = state,
            enableRefresh = false,
            modifier = Modifier.fillMaxSize(),
            onRefresh = {
                viewModel.getUserCollection(loadMore = false, init = false)
            },
            onRetry = {
                viewModel.getUserCollection(
                    loadMore = it == RetryType.LoadMore,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getUserCollection(loadMore = true, init = false)
            }
        ) { index, item ->
            if (item.threadDetails != null) {
                CommonThreadItem(
                    data = item.threadDetails
                )
            }
        }
    }
}