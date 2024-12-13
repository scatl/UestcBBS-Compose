package com.scatl.uestcbbs.compose.module.user.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.api.service.UserService
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.widget.refresh.RefreshIndicator
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadItem
import com.scatl.uestcbbs.compose.module.user.UserProfilePage

/**
 * Created by sca_tl at 2024/7/18 13:56:41
 */
@Composable
fun UserPostsScreen(
    data: UserProfileEntity,
    viewModel: UserViewModel,
    state: LazyListState = rememberLazyListState()
) {
    val userPostData by viewModel.userPostData.collectAsStateWithLifecycle()
    val postType = rememberSaveable { mutableStateOf(UserService.UserPostType.THREAD) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(UserProfilePage.POST)) {
            viewModel.getUserPosts(
                postType = postType.value,
                userSummary = "0",
                visitors = "0",
                loadMore = false,
                init = true
            )
            viewModel.setPageInitialized(UserProfilePage.POST)
        }
    }

    val threads = remember { if (data.userSummary?.threads == 0 || data.userSummary?.threads == null) 0 else data.userSummary.threads }
    val replies = remember { if (data.userSummary?.replies == 0 || data.userSummary?.replies == null) 0 else data.userSummary.replies }
    val digests = remember { if (data.userSummary?.digests == 0 || data.userSummary?.digests == null) 0 else data.userSummary.digests }

    val filter = remember { mutableStateListOf<Pair<String, UserService.UserPostType>>() }
    if (filter.isEmpty()) {
        filter.add(Pair(stringResource(R.string.post).plus(if (threads == 0) "" else "($threads)"), UserService.UserPostType.THREAD))
        filter.add(Pair(stringResource(R.string.reply).plus(if (replies == 0) "" else "($replies)"), UserService.UserPostType.REPLY))
        filter.add(Pair(stringResource(R.string.featured).plus(if (digests == 0) "" else "($digests)"), UserService.UserPostType.FEATURED))
        filter.add(Pair(stringResource(R.string.comment), UserService.UserPostType.COMMENT))
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(top = 5.dp)
    ) {
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = pagePadding)
        ) {
            itemsIndexed(filter) { index, item ->
                FilterChip(
                    label = {
                        Text(
                            text = item.first
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    border = null,
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    selected = postType.value == item.second,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        postType.value = item.second
                        viewModel.getUserPosts(
                            postType = postType.value,
                            userSummary = "0",
                            visitors = "0",
                            loadMore = false,
                            init = true
                        )
                    }
                )
            }
        }

        SwipeRefresh(
            uiState = userPostData,
            listState = state,
            enableRefresh = false,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer),
            onRefresh = {
                viewModel.getUserPosts(
                    postType = postType.value,
                    userSummary = "0",
                    visitors = "0",
                    loadMore = false,
                    init = false
                )
            },
            onRetry = {
                viewModel.getUserPosts(
                    postType = postType.value,
                    userSummary = "0",
                    visitors = "0",
                    loadMore = it != RetryType.Init,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getUserPosts(
                    postType = postType.value,
                    userSummary = "0",
                    visitors = "0",
                    loadMore = true,
                    init = false
                )
            },
            contentType = { _, item ->
                item.itemType
            }
        ) { index, item ->
            when (item.itemType) {
                UserPostData.UserPostDataType.THREAD-> {
                    CommonThreadItem(
                        data = (item as UserPostData.Thread).data
                    )
                }
                UserPostData.UserPostDataType.FEATURED -> {
                    CommonThreadItem(
                        data = (item as UserPostData.Featured).data
                    )
                }
                UserPostData.UserPostDataType.REPLY -> {
                    UserReplyItem(
                        data = (item as UserPostData.Reply).data
                    )
                }
                UserPostData.UserPostDataType.COMMENT -> {
                    UserReplyItem(
                        data = (item as UserPostData.Comment).data,
                        reply = false
                    )
                }
            }
        }
    }

}