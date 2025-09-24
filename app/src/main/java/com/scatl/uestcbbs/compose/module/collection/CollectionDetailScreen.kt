package com.scatl.uestcbbs.compose.module.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionDetailData
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionDetailEntity
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.AuthorLabel
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/10/11 13:56:35
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: Int
) {
    val viewModel: CollectionViewModel = hiltViewModel()
    val collectionDetailData by viewModel.collectionDetailData.collectAsStateWithLifecycle()
    val deleteCollectionData by viewModel.deleteCollectionData.collectAsStateWithLifecycle()
    val collectionInfo by viewModel.collectionInfo.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    LoadInitialDataIfNeeded(collectionId) {
        scope.launchSafety {
            delay(300)
            viewModel.getCollectionDetail(
                id = collectionId,
                loadMore = false,
                init = true
            )
        }
    }

    LaunchedEffect(deleteCollectionData) {
        if (deleteCollectionData.data != null) {
            if (deleteCollectionData.isSuccess) {
                ContextCompat.getString(context, R.string.delete_success).showToast(context)
                showDeleteDialog.value = false
                scope.launchSafety {
                    delay(200)
                    navHostController.popBackStack()
                }
            } else {
                (deleteCollectionData.errorData?.message
                    ?: ContextCompat.getString(context, R.string.delete_fail)
                        ).showToast(context)
            }
            viewModel.resetDeleteCollectionData()
        }
    }

    Column (
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .navigationBarsPadding()
    ) {
        LargeTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            scrollBehavior = scrollBehavior,
            title = {
                Text(
                    text = stringResource(R.string.collection_detail)
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(30.dp)
                        .unboundClickable {
                            navHostController.popBackStack()
                        }
                )
            },
            actions = {
                if (AccountManager.getSignedInAccount()?.uid == collectionInfo.data?.collectionAuthorId.toString()) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = pagePadding)
                            .size(24.dp)
                            .clickable(unbound = true) {
                                showDeleteDialog.value = true
                            }
                    )
                }
            }
        )

        SwipeRefresh(
            uiState = collectionDetailData,
            listState = listState,
            onRefresh = {
                viewModel.getCollectionDetail(
                    id = collectionId,
                    loadMore = false,
                    init = false
                )
            },
            onRetry = {
                viewModel.getCollectionDetail(
                    id = collectionId,
                    loadMore = it == RetryType.LoadMore,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getCollectionDetail(
                    id = collectionId,
                    loadMore = true,
                    init = false
                )
            },
            contentType = { _, item -> item.itemType }
        ) { index, item ->
            when(item) {
                is CollectionDetailData.Info -> {
                    CollectionInfo(
                        modifier = Modifier.animateItem(),
                        viewModel = viewModel,
                        data = item.data
                    )
                }
                is CollectionDetailData.Thread -> {
                    ThreadItem(
                        modifier = Modifier.animateItem(),
                        viewModel = viewModel,
                        item = item.data,
                        collectionDetail = collectionInfo.data,
                        cTid = collectionId
                    )
                }
            }
        }
    }

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(R.string.collection_delete_title),
        text = stringResource(R.string.collection_delete_dsp),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            viewModel.deleteCollection(collectionId)
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CollectionInfo(
    modifier: Modifier,
    viewModel: CollectionViewModel,
    data: CollectionDetailEntity
) {
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val isSubscribe = rememberSaveable { mutableStateOf(data.isSubscribe) }
    val subscribeData = viewModel.subscribeCollectionData.collectAsStateWithLifecycle()

    LaunchedEffect(subscribeData.value) {
        if (subscribeData.value.data != null) {
            if (subscribeData.value.isSuccess) {
                isSubscribe.value = subscribeData.value.data!!
            } else {
                ContextCompat.getString(context, R.string.collection_subscribe_fail).showToast(context)
            }
        }
    }

    Column (
        modifier = modifier
            .padding(horizontal = pagePadding, vertical = 5.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(cardCorner)
            )
            .padding(pagePadding + 5.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(
                text = data.collectionTitle.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (AccountManager.getSignedInAccount()?.uid != data.collectionAuthorId.toString()) {
                Text(
                    text = if (isSubscribe.value) stringResource(R.string.subscribed) else stringResource(R.string.subscribe),
                    fontSize = 12.sp,
                    color = if (isSubscribe.value) MaterialTheme.colorScheme.primary else Color.White,
                    modifier = Modifier
                        .background(
                            color = if (isSubscribe.value) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickable(unbound = false) {
                            viewModel.subscribeCollection(data.collectionId.toIntOrElse(), isSubscribe.value.not())
                        }
                        .padding(
                            horizontal = 20.dp,
                            vertical = 5.dp
                        )
                )
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            IconTitle(
                icon = Icons.Outlined.BookmarkAdded,
                iconTint = MaterialTheme.colorScheme.outline,
                gap = 0.dp,
                iconSize = 14.dp,
                text = stringResource(R.string.collection_subscribe_count, data.subscribeCount.toString()),
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )

            IconTitle(
                icon = Icons.AutoMirrored.Outlined.Article,
                iconTint = MaterialTheme.colorScheme.outline,
                gap = 0.dp,
                iconSize = 14.dp,
                text = stringResource(R.string.collection_post_count, data.threadCount.toString()),
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )

            IconTitle(
                icon = Icons.Outlined.StarOutline,
                iconTint = MaterialTheme.colorScheme.outline,
                gap = 0.dp,
                iconSize = 14.dp,
                text = stringResource(R.string.collection_rate, data.ratingScore.toString()),
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (data.collectionDsp.isNotNullAndEmpty()) {
            Text(
                text = data.collectionDsp.toString(),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 14.sp
            )
        }

        if (data.collectionTags.isNotNullAndEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
            ) {
                data.collectionTags?.forEach {
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
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.collection_creator),
                fontSize = 14.sp,
                modifier = Modifier
                    .alpha(alpha = 0.7f)
            )
            AuthorLabel(
                uid = data.collectionAuthorId,
                name = data.collectionAuthorName,
            )
            if (data.maintainer.isNotNullAndEmpty()) {
                Spacer(
                    modifier = Modifier
                        .width(7.dp)
                        .height(10.dp)
                        .padding(horizontal = 3.dp)
                        .background(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                )
                Text(
                    text = stringResource(R.string.collection_maintainer),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .alpha(alpha = 0.7f)
                )
                data.maintainer.forEach {
                    AuthorLabel(
                        uid = it.userId,
                        name = it.userName,
                    )
                }
            }
        }

        if (data.authorOtherCollection.isNotNullAndEmpty()) {
            HorizontalDivider(
                thickness = 0.2.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Text(
                text = stringResource(R.string.collection_author_other_dsp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                data.authorOtherCollection.forEach {
                    Text(
                        text = it.name.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(50)
                            )
                            .clip(
                                shape = RoundedCornerShape(50)
                            )
                            .clickable(unbound = false) {
                                navHostController.navigate(Router.CollectionDetailRouterEntity(it.cid.toIntOrElse()))
                            }
                            .padding(horizontal = 10.dp, vertical = 1.dp)
                    )
                }
            }
        }

        if (data.recentSubscriber.isNotNullAndEmpty()) {
            HorizontalDivider(
                thickness = 0.2.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Text(
                text = stringResource(R.string.collection_recent_subscribe_dsp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                data.recentSubscriber.forEach {
                    AuthorLabel(
                        uid = it.userId,
                        name = it.userName
                    )
                }
            }
        }
    }
}

@Composable
private fun ThreadItem(
    modifier: Modifier,
    viewModel: CollectionViewModel,
    collectionDetail: CollectionDetailEntity?,
    item: CollectionDetailEntity.ThreadItem,
    cTid: Int
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val removeCollectionPostData by viewModel.removeCollectionPostData.collectAsStateWithLifecycle()
    val self = rememberSaveable { mutableStateOf(AccountManager.getSignedInAccount()?.uid == collectionDetail?.collectionAuthorId.toString()) }

    LaunchedEffect(removeCollectionPostData) {
        if (removeCollectionPostData.data != null) {
            if (removeCollectionPostData.isSuccess && removeCollectionPostData.data == item.topicId) {
                ContextCompat.getString(context, R.string.collection_post_remove_success).showToast(context)
                showDeleteDialog.value = false
                viewModel.removePost(item.topicId.toIntOrElse())
            } else {
                (removeCollectionPostData.errorData?.message ?:
                    ContextCompat.getString(context, R.string.collection_post_remove_fail)
                ).showToast(context)
            }
            viewModel.resetRemoveCollectionPostData()
        }
    }

    Column (
        modifier = modifier
            .commonCardBg (
                onLongClick = {
                    if (self.value) {
                        showDeleteDialog.value = true
                    }
                }
            ) {
                navHostController.navigate(Router.ThreadDetailRouterEntity(
                    id = item.topicId.toIntOrElse()
                ))
            }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = item.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(50))
            )
            Column {
                Text(
                    text = item.authorName.toString(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = item.postDate.toString(),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier
                        .alpha(0.5f)
                        .padding(start = 1.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.topicTitle.toString()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.latest_reply_at, item.lastPostDate.toString()),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.alpha(0.5f)
            )
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.commentCount} ${stringResource(R.string.reply)}",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${item.viewCount} ${stringResource(R.string.views)}",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(R.string.collection_post_remove_title),
        text = stringResource(R.string.collection_post_remove_dsp),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            viewModel.removeCollectionPost(cTid, item.topicId.toIntOrElse())
        }
    )
}