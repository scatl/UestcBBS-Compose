package com.scatl.uestcbbs.compose.module.user.collect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CollectionEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadItem
import com.scatl.uestcbbs.compose.module.user.UserProfilePage
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.module.video.formatTime
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.IconPosition
import com.scatl.uestcbbs.compose.widget.IconTitle
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
            when(item.itemType) {
                UserCollectionData.UserCollectionDataType.COLLECTION_TITLE -> {
                    Text(
                        text = "专辑",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(pagePadding)
                    )
                }

                UserCollectionData.UserCollectionDataType.COLLECTION -> {
                    Collection((item as UserCollectionData.Collection).data)
                }

                UserCollectionData.UserCollectionDataType.THREAD_TITLE -> {
                    Text(
                        text = "帖子",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(pagePadding)
                    )
                }

                UserCollectionData.UserCollectionDataType.THREAD -> {
                    CommonThreadItem(
                        data = (item as UserCollectionData.Thread).data.threadDetails
                    )
                }
            }
        }
    }
}

@Composable
private fun Collection(collectionEntity: CollectionEntity) {
    val navHostController = LocalNavController.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .commonCardBg {
                navHostController.navigate(
                    Router.CollectionDetailRouterEntity(
                        id = collectionEntity.collectionId.toIntOrElse()
                    )
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = collectionEntity.name.toString(),
                modifier = Modifier
                    .weight(1f, true)
                    .horizontalScroll(rememberScrollState())
            )

            Spacer(modifier = Modifier.width(20.dp))

            IconTitle(
                icon = Icons.Outlined.BookmarkAdd,
                iconSize = 22.dp,
                iconTint = MaterialTheme.colorScheme.primary,
                iconPosition = IconPosition.LEFT,
                text = collectionEntity.follows.toString(),
                gap = 0.dp,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        if (collectionEntity.description.isNotNullAndEmpty()) {
            Text(
                text = collectionEntity.description.toString(),
                fontSize = 14.sp,
                modifier = Modifier.alpha(alpha = 0.5f)
            )
        }

        if (collectionEntity.keyword.isNotNullAndEmpty()) {
            Spacer(modifier = Modifier.height(5.dp))
            Row (
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
            ) {
                collectionEntity.keyword.split(",").forEach {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        lineHeight = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "最近更新：${formatTimestamp(collectionEntity.lastUpdate, context)}",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            modifier = Modifier
                .alpha(0.7f)
                .padding(start = 1.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${collectionEntity.threads}主题 · ${collectionEntity.follows}订阅 · ${collectionEntity.comments}评论",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            modifier = Modifier
                .alpha(0.7f)
                .padding(start = 1.dp)
        )
    }
}