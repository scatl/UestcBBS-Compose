package com.scatl.uestcbbs.compose.module.post.item

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.VerticalAlignTop
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.markdown.MarkdownDefaults
import com.scatl.markdown.MarkdownText
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.db.entity.VotedPostDBEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.copyToClipBoard
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.module.post.commentrate.CommentBottomSheet
import com.scatl.uestcbbs.compose.module.post.screen.CommentInfo
import com.scatl.uestcbbs.compose.module.post.DeletePostDialog
import com.scatl.uestcbbs.compose.module.post.screen.MoreOptionItem
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.module.post.screen.RateInfo
import com.scatl.uestcbbs.compose.module.post.bottomsheet.ReportBottomSheet
import com.scatl.uestcbbs.compose.module.post.commentrate.RateBottomSheet
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.router.linkNavigate
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.HtmlUtil
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.IconTitle
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/8/19 15:42:19
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ThreadReplyItem(
    modifier: Modifier,
    data: ThreadDetailEntity,
    item: ThreadDetailEntity.Row,
    index: Int,
    viewModel: PostViewModel,
    showSnapshot: MutableState<Boolean>,
    filerAuthorClick: (authorId: String?, authorName: String?) -> Unit,
    onDeleteSuccess: ((pid: String?) -> Unit)? = null,
    onCreatePost: () -> Unit
) {
    val navHostController = LocalNavController.current
    val uriHandler = LocalUriHandler.current

    val supportData by viewModel.supportData(item.postId.toString()).collectAsStateWithLifecycle()
    val supportCount = rememberSaveable { mutableIntStateOf(item.support.toIntOrElse()) }
    val againstCount = rememberSaveable { mutableIntStateOf(item.oppose.toIntOrElse()) }

    var updateCommentFlag by remember { mutableStateOf("") }

    val supported = remember {
        mutableStateOf(supportData.data?.support == true ||
                viewModel.postRepository.dataBase.getVotedPostDao().findFirstById(item.postId.toString())?.support == true
        )
    }
    val againsted = remember {
        mutableStateOf(supportData.data?.support == false ||
                viewModel.postRepository.dataBase.getVotedPostDao().findFirstById(item.postId.toString())?.against == true
        )
    }

    val moreOptionsBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val openBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(supportData.data) {
        if (viewModel.postRepository.dataBase.getVotedPostDao().findFirstById(item.postId.toString()) == null
            && supportData.data != null
            && supportData.data?.success == true
        ) {
            if (supportData.data?.support == true) {
                supportCount.intValue += 1
                supported.value = true
            } else {
                againstCount.intValue += 1
                againsted.value = true
            }
            val dbEntity = VotedPostDBEntity(
                pid = item.postId.toString(),
                support = supportData.data?.support == true,
                against = supportData.data?.support == false
            )
            viewModel.postRepository.dataBase.getVotedPostDao().insert(dbEntity)
        }
    }

    val highLight = item.highLight.collectAsState()
    val startColor = MaterialTheme.colorScheme.background
    val middleColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val targetColor = remember { mutableStateOf(startColor) }

    val color by animateColorAsState(
        targetValue = targetColor.value,
        animationSpec = tween(1500),
        label = ""
    )

    LaunchedEffect(highLight) {
        snapshotFlow { highLight.value }
            .collect {
                if (it) {
                    targetColor.value = middleColor
                    delay(1500)
                    targetColor.value = startColor
                    delay(1500)
                    item.highLight.value = false
                } else {
                    targetColor.value = startColor
                }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = color)
            .combinedClickable(
                onLongClick = {
                    openBottomSheet.value = true
                },
                onClick = {
                    onCreatePost.invoke()
                }
            )
            .padding(horizontal = pagePadding + 5.dp)
            .padding(top = pagePadding * 2)
    ) {
        Column {
            Row (
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                AsyncImage(
                    model = if (item.isAnonymous == 1) {
                        0.toAvatarUrl()
                    } else {
                        item.authorId.toAvatarUrl()
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(35.dp)
                        .clip(shape = RoundedCornerShape(50))
                        .clickable(enabled = true) {
                            navHostController.navigate(
                                Router.UserProfileRouterEntity(
                                    uid = item.authorId,
                                    name = item.author.toString()
                                )
                            )
                        }
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                if (item.isAnonymous == 1) {
                                    append(stringResource(R.string.anonymous))
                                    if (item.authorId.toString() == AccountManager.getSignedInAccount()?.uid) {
                                        val txt = "(${stringResource(R.string.self)})"
                                        append(txt)
                                        addStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            ),
                                            start = stringResource(R.string.anonymous).length,
                                            end = stringResource(R.string.anonymous).length + txt.length
                                        )
                                    }
                                } else {
                                    append(item.author.toString())
                                }
                            },
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.alpha(alpha = 0.7f)
                        )

                        if (data.thread?.authorId != 0 && data.thread?.authorId == item.authorId) {
                            Box (
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .fillMaxHeight()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 5.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.author),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }

                        if (item.authorDetails != null) {
                            Box (
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .fillMaxHeight()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 5.dp)
                            ) {
                                Text(
                                    text = if (item.authorDetails.groupTitle?.contains("Lv") == false) {
                                        item.authorDetails.groupTitle.toString()
                                    } else {
                                        "Lv${item.authorDetails.levelId}"
                                    },
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }

                        if (item.authorDetails?.groupSubtitle.isNotNullAndEmpty()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 5.dp)
                            ) {
                                Text(
                                    text = item.authorDetails?.groupSubtitle.toString(),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }

                        if (item.pinned == true) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconTitle(
                                icon = Icons.Outlined.VerticalAlignTop,
                                iconSize = 12.dp,
                                iconTint = MaterialTheme.colorScheme.primary,
                                text = stringResource(R.string.top_stick),
                                textStyle = TextStyle(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                gap = 0.dp,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (item.blocked == true) {
                        Text(
                            text = stringResource(R.string.thread_detail_post_blocked),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        if (item.format == 2) {
                            MarkdownText(
                                markdown = HtmlUtil.formatMarkdown(item.message.toString(), item.attachments),
                                theme = MarkdownDefaults.defaultTheme(),
                                onLinkClicked = {
                                    linkNavigate(
                                        url = it,
                                        openBrowserIfNotMatch = true,
                                        navHostController = navHostController,
                                        uriHandler = uriHandler
                                    )
                                }
                            )
                        } else {
                            Text(
                                text = item.message.toString()
                            )
//                            MarkdownText(
//                                markdown = "<blockquote>www</blockquote>",
//                                theme = MarkdownDefaults.defaultTheme(),
//                                onLinkClicked = {
//                                    linkNavigate(
//                                        url = it,
//                                        openBrowserIfNotMatch = true,
//                                        navHostController = navHostController,
//                                        uriHandler = uriHandler
//                                    )
//                                }
//                            )
                        }
                    }

                    if (item.replyCreditAmount.toIntOrElse() > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.thread_detail_reply_award, item.replyCreditAmount.toString(), item.replyCreditName.toString()),
                            color = LocalCustomColors.current.threadDetailReplyAward,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            lineHeight = 12.sp
                        )
                    }

                    if (data.thread?.rushReply?.targetPositions?.contains(item.position.toString()) == true ||
                        data.thread?.rushReply?.targetPositions?.find { it.startsWith("*") && it.endsWith(item.position.toString()) } != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.thread_detail_reply_win_target_floor),
                            color = LocalCustomColors.current.threadDetailReplyAward,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            lineHeight = 12.sp
                        )
                    }

                    key(updateCommentFlag) {
                        CommentInfo(
                            data = item.commentAndRate,
                            threadId = item.threadId.toString(),
                            postId = item.postId.toString()
                        )
                    }

                    RateInfo(
                        data = item.commentAndRate,
                        threadId = item.threadId.toString(),
                        postId = item.postId.toString()
                    )

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTimestamp(item.dateline, LocalContext.current),
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                modifier = Modifier.alpha(0.5f)
                            )

                            Text(
                                text = " · ",
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alpha(0.5f)
                            )

                            Text(
                                text = "#${item.position.toString()}",
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            IconTitle(
                                icon = Icons.Outlined.ThumbUp,
                                iconSize = 16.dp,
                                iconTint = if (supported.value) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                },
                                text = if (supportCount.intValue.toIntOrElse() > 0) {
                                    supportCount.intValue.toString()
                                } else {
                                    ""
                                },
                                gap = 2.dp,
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color = if (supported.value) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                    }
                                ),
                                modifier = Modifier
                                    .clickable(unbound = true) {
                                        if (!showSnapshot.value) {
                                            viewModel.support(
                                                tid = data.thread?.threadId.toString(),
                                                pid = item.postId.toString(),
                                                support = true
                                            )
                                        }
                                    }
                                    .alpha(alpha = if (supported.value) 0.5f else 0.3f)
                            )

                            IconTitle(
                                icon = Icons.Outlined.ThumbDown,
                                iconSize = 16.dp,
                                iconTint = if (againsted.value) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                },
                                text = if (againstCount.intValue.toIntOrElse() > 0) {
                                    againstCount.intValue.toString()
                                } else {
                                    ""
                                },
                                gap = 2.dp,
                                textStyle = TextStyle(
                                    fontSize = 12.sp,
                                    color = if (againsted.value) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onBackground
                                    }
                                ),
                                modifier = Modifier
                                    .clickable(unbound = true) {
                                        if (!showSnapshot.value) {
                                            viewModel.support(
                                                tid = data.thread?.threadId.toString(),
                                                pid = item.postId.toString(),
                                                support = false
                                            )
                                        }
                                    }
                                    .alpha(alpha = if (againsted.value) 0.5f else 0.3f)
                            )

                            Icon(
                                imageVector = Icons.Outlined.MoreHoriz,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable(unbound = true) {
                                        openBottomSheet.value = true
                                    }
                                    .alpha(alpha = 0.3f)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.2.dp,
                modifier = Modifier
                    .padding(start = 40.dp, top = pagePadding)
            )
        }

        if (supportCount.intValue.toIntOrElse() > 10) {
            Image(
                painter = painterResource(id = R.drawable.ic_hot_comment),
                contentDescription = null,
                //colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(35.dp)
            )
        }
    }

    MoreOptions(
        sheetState = moreOptionsBottomSheetState,
        openSheet = openBottomSheet,
        data = item,
        viewModel = viewModel,
        showSnapshot = showSnapshot,
        filerAuthorClick = filerAuthorClick,
        onDeleteSuccess = onDeleteSuccess,
        onCommentSuccess = { pid, msg ->
            viewModel.insertComment(pid, msg)
            updateCommentFlag = System.currentTimeMillis().toString()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptions(
    sheetState: SheetState,
    openSheet: MutableState<Boolean>,
    data: ThreadDetailEntity.Row,
    viewModel: PostViewModel,
    showSnapshot: MutableState<Boolean>,
    filerAuthorClick: (authorId: String?, authorName: String?) -> Unit,
    onDeleteSuccess: ((pid: String?) -> Unit)? = null,
    onCommentSuccess: ((pid: Int, message: String) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val self = rememberSaveable { mutableStateOf(AccountManager.getSignedInAccount()?.uid == data.authorId.toString()) }
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    val openCommentBottomSheet = rememberSaveable { mutableStateOf(false) }
    val openRateBottomSheet = rememberSaveable { mutableStateOf(false) }
    val openReportBottomSheet = rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = openSheet.value) {
        scope.launchSafety {
            sheetState.hide()
        }
    }

    fun hide() {
        scope.launchSafety {
            sheetState.hide()
            openSheet.value = false
        }
    }

    if (openSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openSheet.value = false
            },
            sheetState = sheetState,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                state = rememberLazyGridState(),
                userScrollEnabled = false
            ) {
                if (showSnapshot.value.not() && data.authorId != 0) {
                    item {
                        MoreOptionItem(
                            text = stringResource(if (self.value) {
                                R.string.thread_detail_author_post_only_my
                            } else {
                                R.string.thread_detail_author_post_only
                            }),
                            icon = Icons.Outlined.PersonSearch
                        ) {
                            hide()
                            filerAuthorClick.invoke(data.authorId.toString(), data.author.toString())
                        }
                    }
                }

                if (showSnapshot.value.not() && self.value) {
                    item {
                        MoreOptionItem(
                            text = stringResource(id = R.string.delete),
                            icon = Icons.Outlined.DeleteOutline
                        ) {
                            hide()
                            showDeleteDialog.value = true
                        }
                    }
                }

                if (showSnapshot.value.not()) {
                    item {
                        MoreOptionItem(
                            text = "点评",
                            icon = Icons.Outlined.RateReview
                        ) {
                            hide()
                            openCommentBottomSheet.value = true
                        }
                    }
                }

                if (showSnapshot.value.not() && self.value.not()) {
                    item {
                        MoreOptionItem(
                            text = "评分",
                            icon = Icons.Outlined.WaterDrop
                        ) {
                            hide()
                            openRateBottomSheet.value = true
                        }
                    }
                }

                item {
                    MoreOptionItem(
                        text = stringResource(id = R.string.copy_link),
                        icon = Icons.Outlined.ContentCopy
                    ) {
                        hide()
                        Constants.BBS_URL.plus("/goto/${data.postId}").copyToClipBoard(context)
                    }
                }

                item {
                    MoreOptionItem(
                        text = stringResource(id = R.string.open_in_browser),
                        icon = Icons.Outlined.OpenInBrowser
                    ) {
                        hide()
                        uriHandler.openUri(Constants.BBS_URL.plus("/goto/${data.postId}"))
                    }
                }

                if (showSnapshot.value.not() && self.value.not()) {
                    item {
                        MoreOptionItem(
                            text = stringResource(id = R.string.report),
                            icon = Icons.Outlined.ErrorOutline
                        ) {
                            hide()
                            openReportBottomSheet.value = true
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        DeletePostDialog(
            showDialog = showDeleteDialog,
            pid = data.postId.toString(),
            tid = data.threadId.toString(),
            viewModel = viewModel,
            onSuccess = {
                onDeleteSuccess?.invoke(it)
            }
        )
    }

    if (openCommentBottomSheet.value) {
        CommentBottomSheet(
            openCommentBottomSheet = openCommentBottomSheet,
            threadId = data.threadId.toIntOrElse(),
            postId = data.postId.toIntOrElse(),
            onSuccess = { pid, msg ->
                onCommentSuccess?.invoke(pid, msg)
            }
        )
    }

    if (openRateBottomSheet.value) {
        RateBottomSheet(
            openRateBottomSheet = openRateBottomSheet,
            threadId = data.threadId.toIntOrElse(),
            postId = data.postId.toIntOrElse(),
            onSuccess = { pid ->

            }
        )
    }

    if (openReportBottomSheet.value) {
        ReportBottomSheet(
            openReportBottomSheet = openReportBottomSheet,
            pid = data.postId.toIntOrElse(),
            fid = data.forumId.toIntOrElse()
        )
    }
}