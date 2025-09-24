package com.scatl.uestcbbs.compose.module.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.collectionOrderSaver
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionListEntity
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.IconPosition
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/10/11 9:13:16
 */
@Composable
fun CollectionListScreen(
    collectionType: CollectionType
) {
    val viewModel: CollectionViewModel = hiltViewModel()
    val collectionListData by viewModel.collectionListData(collectionType).collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val currentOrderBy = rememberSaveable(stateSaver = collectionOrderSaver) { mutableStateOf(CollectionOrder.FOLLOW_NUM) }

    LaunchedEffect(key1 = collectionType) {
        if (!viewModel.isPageInitialized(collectionType)) {
            viewModel.getCollectionList(
                op = collectionType,
                order = currentOrderBy.value,
                loadMore = false,
                init = true
            )
            viewModel.setPageInitialized(collectionType)
        }
    }

    Column {
        if (collectionType == CollectionType.ALL) {
            Filter(
                viewModel = viewModel,
                currentOrderBy = currentOrderBy
            )
        }

        SwipeRefresh(
            uiState = collectionListData,
            listState = listState,
            onRefresh = {
                viewModel.getCollectionList(
                    op = collectionType,
                    order = currentOrderBy.value,
                    loadMore = false,
                    init = false
                )
            },
            onRetry = {
                viewModel.getCollectionList(
                    op = collectionType,
                    order = currentOrderBy.value,
                    loadMore = it == RetryType.LoadMore,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getCollectionList(
                    op = collectionType,
                    order = currentOrderBy.value,
                    loadMore = true,
                    init = false
                )
            }
        ) { index, item ->
            Item(
                data = item,
                type = collectionType
            )
        }
    }
}

@Composable
private fun Filter(
    viewModel: CollectionViewModel,
    currentOrderBy: MutableState<CollectionOrder>
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val titles = remember {
        mutableStateListOf(
            Pair(ContextCompat.getString(context, R.string.collection_sort_by_subscribe), CollectionOrder.FOLLOW_NUM),
            Pair(ContextCompat.getString(context, R.string.collection_sort_by_create_time), CollectionOrder.CREATE_TIME),
            Pair(ContextCompat.getString(context, R.string.collection_sort_by_thread_count), CollectionOrder.THREAD_NUM),
            Pair(ContextCompat.getString(context, R.string.collection_sort_by_comment_count), CollectionOrder.COMMENT_NUM)
        )
    }

    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(horizontal = pagePadding)
    ) {
        itemsIndexed(titles) { index, item ->
            FilterChip(
                label = {
                    Text(
                        text = item.first,
                        fontSize = 12.sp
                    )
                },
                shape = RoundedCornerShape(15.dp),
                border = null,
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                selected = currentOrderBy.value == item.second,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    currentOrderBy.value = item.second
                    viewModel.getCollectionList(
                        op = CollectionType.ALL,
                        order = currentOrderBy.value,
                        loadMore = false,
                        init = false
                    )
                }
            )
        }
    }
}

@Composable
private fun Item(
    data: CollectionListEntity,
    type: CollectionType
) {
    val navHostController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = pagePadding)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(cardCorner)
            )
            .clip(shape = RoundedCornerShape(cardCorner))
            .clickable {
                navHostController.navigate(Router.CollectionDetailRouterEntity(data.collectionId))
            }
            .padding(pagePadding + 5.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    val text = if (data.createByMe) {
                        stringResource(R.string.collection_item_create)
                    } else if (data.subscribeByMe) {
                        stringResource(R.string.collection_item_subscribe)
                    } else {
                        ""
                    }

                    append(text)
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        start = 0,
                        end = text.length
                    )
                    append(data.collectionTitle.toString())
                },
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(20.dp))

            IconTitle(
                icon = Icons.Outlined.BookmarkAdd,
                iconSize = 22.dp,
                iconTint = MaterialTheme.colorScheme.primary,
                iconPosition = IconPosition.RIGHT,
                text = data.subscribeCount.toString(),
                gap = 0.dp,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        if (data.collectionDsp.isNotNullAndEmpty()) {
            Text(
                text = data.collectionDsp.toString(),
                fontSize = 14.sp,
                modifier = Modifier.alpha(alpha = 0.5f)
            )
        }

        if (data.collectionTags.isNotNullAndEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
            ) {
                data.collectionTags.forEach {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            AsyncImage(
                model = data.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
                    .clip(shape = RoundedCornerShape(50))
                    .clickable {
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = data.authorId,
                                name = data.authorName.toString()
                            )
                        )
                    }
            )

            Text(
                text = data.authorName.toString(),
                fontSize = 14.sp,
                modifier = Modifier.alpha(alpha = 0.7f)
            )

            Text(
                text = "·",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(start = 1.dp)
            )

            Text(
                text = stringResource(id = R.string.thread_detail_collection_count, data.postCount.toString()),
                fontSize = 14.sp,
                modifier = Modifier.alpha(alpha = 0.7f)
            )

            if (data.hasUnreadPost) {
                Text(
                    text = buildAnnotatedString {
                        append("· ${stringResource(R.string.collection_item_has_update)}")
                        addStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            start = 1,
                            end = 5
                        )
                    },
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier
                        .alpha(0.7f)
                        .padding(start = 1.dp)
                )
            }
        }
    }
}