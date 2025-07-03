package com.scatl.uestcbbs.compose.module.post.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.ForumPicture
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.PostCommentAndRateEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.api.entity.request.VoteRequestEntity
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.copyToClipBoard
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.ext.setSecureFlag
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.sp2px
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.module.post.DeletePostDialog
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.module.post.bottomsheet.AddToCollectionBottomSheet
import com.scatl.uestcbbs.compose.module.post.bottomsheet.ReportBottomSheet
import com.scatl.uestcbbs.compose.module.post.commentrate.CommentBottomSheet
import com.scatl.uestcbbs.compose.module.post.commentrate.CommentRateType
import com.scatl.uestcbbs.compose.module.post.commentrate.RateBottomSheet
import com.scatl.uestcbbs.compose.module.post.item.ThreadReplyItem
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadTitle
import com.scatl.uestcbbs.compose.module.snapshot.SnapshotViewModel
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.util.formatTimestampYMDHMS
import com.scatl.uestcbbs.compose.widget.CommonConfirmDialog
import com.scatl.uestcbbs.compose.widget.IconPosition
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.LikeDislikeProgressBar
import com.scatl.uestcbbs.compose.widget.LoadingDialog
import com.scatl.uestcbbs.compose.widget.RoundCheckBox
import com.scatl.uestcbbs.compose.widget.RoundCheckBoxDefaults
import com.scatl.uestcbbs.compose.widget.StatusLayout
import com.scatl.uestcbbs.compose.widget.StickyLayout
import com.scatl.uestcbbs.compose.widget.StickyLayoutController
import com.scatl.uestcbbs.compose.widget.WatermarkDrawable
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import com.scatl.uestcbbs.compose.widget.web.LocalHtmlWebView
import kotlinx.coroutines.delay
import okhttp3.internal.toLongOrDefault

/**
 * Created by sca_tl at 2024/7/15 10:00:21
 */

const val TAG = "ThreadDetailScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadDetailScreen(
    routerEntity: Router.ThreadDetailRouterEntity
) {
    val viewModel: PostViewModel = hiltViewModel()
    val context = LocalContext.current
    val threadDetailData by viewModel.threadDetailData.collectAsStateWithLifecycle()
    val stickyLayoutController = rememberUpdatedState(remember { StickyLayoutController() })
    val progress = remember { mutableFloatStateOf(0f) }
    val headContentHeight = rememberSaveable { mutableFloatStateOf(0f) }
    val barContentHeight = rememberSaveable { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val moreOptionsBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val openMoreOptionsBottomSheet = rememberSaveable { mutableStateOf(false) }

    val openCommentBottomSheet = rememberSaveable { mutableStateOf(false) }
    val openRateBottomSheet = rememberSaveable { mutableStateOf(false) }
    val openCreatePostScreen = rememberSaveable { mutableStateOf(false) }
    val openReportBottomSheet = rememberSaveable { mutableStateOf(false) }

    val showSnapshotData = rememberSaveable { mutableStateOf(false) }
    val headGraphicsLayer = rememberGraphicsLayer()
    val scrollDirection = rememberSaveable { mutableStateOf<Int?>(null) }

    val currentCreatePostData = rememberSaveable { mutableStateOf(CreatePostEntity()) }

    suspend fun getData() {
        val success = viewModel.getSnapshotData(routerEntity.snapshot)
        showSnapshotData.value = success
        if (success.not()) {
            if (routerEntity.snapshot.isNotNullAndEmpty()) {
                ContextCompat
                    .getString(context, R.string.snapshot_get_error_show_realtime)
                    .showToast(context)
            }

            if (routerEntity.id <= 0) {
                viewModel.getThreadDetailByPid(routerEntity.pid.toString())
            } else {
                viewModel.getThreadDetail(routerEntity.id.toString())
            }
        }
    }

    LoadInitialDataIfNeeded(key = routerEntity.id) {
        scope.launchSafety {
            delay(300)
            getData()
        }
    }

    DisposableEffect(threadDetailData.data) {
        val activity = context as ComponentActivity
        activity.setSecureFlag(
            Constants.INTERNAL_FIDS.contains(threadDetailData.data?.thread?.forumId.toIntOrElse())
                    || showSnapshotData.value
        )
        onDispose {
            activity.setSecureFlag(false)
        }
    }

//    BackHandler (
//        enabled = openCreatePostScreen.value
//    ) {
//        openCreatePostScreen.value = false
//    }

    StatusLayout(
        uiState = threadDetailData,
        onRetry = {
            scope.launchSafety {
                getData()
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()

                .navigationBarsPadding()
        ) {
            WaterMark(
                showSnapshot = showSnapshotData
            )

            StickyLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (showSnapshotData.value) 0.95f else 0.975f)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                controller = stickyLayoutController,
                headContent = {
                    HeadContent(
                        data = threadDetailData.data,
                        stickyLayoutController = stickyLayoutController,
                        viewModel = viewModel,
                        progress = progress,
                        barHeight = barContentHeight,
                        headGraphicsLayer = headGraphicsLayer,
                        showSnapshot = showSnapshotData
                    )
                },
                barContent = {
                    TopBarContent(
                        data = threadDetailData.data,
                        stickyLayoutController = stickyLayoutController,
                        progress = progress,
                        headHeight = headContentHeight,
                        barHeight = barContentHeight,
                        sheetState = moreOptionsBottomSheetState,
                        openMoreOptionsBottomSheet = openMoreOptionsBottomSheet,
                        showSnapshot = showSnapshotData
                    )
                },
                bodyContent = {
                    BodyContent(
                        data = threadDetailData.data,
                        listState = listState,
                        viewModel = viewModel,
                        showSnapshot = showSnapshotData,
                        openCreatePostScreen = openCreatePostScreen,
                        currentCreatePostData = currentCreatePostData,
                        targetPid = routerEntity.pid,
                        stickyLayoutController = stickyLayoutController,
                        onTabSelected = { _, state ->
                            stickyLayoutController.value.bodyStateChange?.invoke(state)
                        }
                    )
                },
                onProgress = { percent, _ ->
                    progress.floatValue = percent
                },
                onSizeChanged = { barHeight, headHeight ->
                    headContentHeight.floatValue = headHeight
                    barContentHeight.floatValue = barHeight
                },
                onScrollDirectionChange = {
                    scrollDirection.value = it
                }
            )

            if (showSnapshotData.value.not()) {
                BottomBar(
                    data = threadDetailData.data,
                    viewModel = viewModel,
                    openCommentBottomSheet = openCommentBottomSheet,
                    openRateBottomSheet = openRateBottomSheet,
                    openCreatePostScreen = openCreatePostScreen,
                    currentCreatePostData = currentCreatePostData,
                    scrollDirection = scrollDirection,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            MoreOptions(
                sheetState = moreOptionsBottomSheetState,
                openMoreOptionsBottomSheet = openMoreOptionsBottomSheet,
                openReportBottomSheet = openReportBottomSheet,
                data = threadDetailData.data,
                viewModel = viewModel,
                showSnapshot = showSnapshotData,
                headGraphicsLayer = headGraphicsLayer
            )

            if (openCommentBottomSheet.value) {
                CommentBottomSheet(
                    openCommentBottomSheet = openCommentBottomSheet,
                    threadId = threadDetailData.data?.thread?.threadId.toIntOrElse(),
                    postId = threadDetailData.data?.thread?.postId.toIntOrElse(),
                    onSuccess = { pid, msg ->
                        viewModel.insertComment(pid, msg)
                    }
                )
            }

            if (openRateBottomSheet.value) {
                RateBottomSheet(
                    openRateBottomSheet = openRateBottomSheet,
                    threadId = threadDetailData.data?.thread?.threadId.toIntOrElse(),
                    postId = threadDetailData.data?.thread?.postId.toIntOrElse(),
                    onSuccess = { pid ->

                    }
                )
            }

            if (openReportBottomSheet.value) {
                ReportBottomSheet(
                    openReportBottomSheet = openReportBottomSheet,
                    pid = threadDetailData.data?.thread?.postId.toIntOrElse(),
                    fid = threadDetailData.data?.forum?.fid.toIntOrElse()
                )
            }

            CreatePostScreen(
                data = currentCreatePostData.value,
                show = openCreatePostScreen
            )
        }
    }
}

@Composable
private fun WaterMark(
    showSnapshot: MutableState<Boolean>
) {
    val drawable = WatermarkDrawable().apply {
        mText = AccountManager.getSignedInAccount()?.uid.toString()
        mTextColor = MaterialTheme.colorScheme.onBackground.toArgb()
        mTextSize = if (showSnapshot.value) 30.sp2px else 20.sp2px
        mRotation = -15f
        mXSpace = 1.4f
        mYSpace = 15
    }
    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerSize.width
    val screenHeight = windowInfo.containerSize.height
    Image(
        bitmap = drawable.toBitmap(screenWidth, screenHeight).asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun HeadContent(
    data: ThreadDetailEntity?,
    stickyLayoutController: State<StickyLayoutController>,
    viewModel: PostViewModel,
    progress: MutableFloatState,
    barHeight: MutableFloatState,
    showSnapshot: MutableState<Boolean>,
    headGraphicsLayer: GraphicsLayer
) {
    if (data == null) {
        return
    }

    Box (
        modifier = Modifier
//            .drawWithContent {
//                headGraphicsLayer.record {
//                    this@drawWithContent.drawContent()
//                }
//                drawLayer(headGraphicsLayer)
//            }
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = barHeight.floatValue.px2dp)
    ) {
        Column (
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            ThreadInfo(
                data = data,
                showSnapshot = showSnapshot,
                viewModel = viewModel
            )

            HorizontalDivider(
                thickness = 0.2.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Content(
                data = data,
                showSnapshot = showSnapshot,
                viewModel = viewModel
            )

            PollInfo(
                data = data,
                showSnapshot = showSnapshot,
                viewModel = viewModel
            )

            CommentInfo(
                data = data.rows?.getOrNull(0)?.commentAndRate,
                threadId = data.thread?.threadId.toString(),
                postId = data.rows?.getOrNull(0)?.postId.toString()
            )

            RateInfo(
                data = data.rows?.getOrNull(0)?.commentAndRate,
                threadId = data.thread?.threadId.toString(),
                postId = data.rows?.getOrNull(0)?.postId.toString()
            )

            Collection(
                data = data
            )

            Spacer(modifier = Modifier.height(20.dp))

            SupportInfo(
                data = data,
                viewModel = viewModel,
                showSnapshot = showSnapshot
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        HorizontalDivider(
            thickness = 10.dp,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarContent(
    data: ThreadDetailEntity?,
    stickyLayoutController: State<StickyLayoutController>,
    progress: MutableFloatState,
    headHeight: MutableFloatState,
    barHeight: MutableFloatState,
    showSnapshot: MutableState<Boolean>,
    sheetState: SheetState,
    openMoreOptionsBottomSheet: MutableState<Boolean>,
) {
    if (data == null) {
        return
    }
    val navHostController = LocalNavController.current
    val arrowRotated = remember { mutableStateOf(false) }

    val rotationDegree = animateFloatAsState(
        targetValue = if (arrowRotated.value) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "arrow_rotation"
    )

    Column {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.thread_detail_title),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = pagePadding)
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.content_dsp_thread_detail_back),
                    modifier = Modifier
                        .padding(start = pagePadding)
                        .size(30.dp)
                        .unboundClickable {
                            navHostController.popBackStack()
                        }
                )
            },
            actions = {
                Icon(
                    imageVector = Icons.Outlined.KeyboardDoubleArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(rotationDegree.value)
                        .unboundClickable {
                            arrowRotated.value = !arrowRotated.value
                            if (arrowRotated.value) {
                                stickyLayoutController.value.scrollToBody?.invoke(300)
                            } else {
                                stickyLayoutController.value.scrollToTop?.invoke(300)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(pagePadding))
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .unboundClickable {
                            openMoreOptionsBottomSheet.value = true
                        }
                )
                Spacer(modifier = Modifier.width(pagePadding))
            }
        )

        ReadProgressIndicator(
            progress = progress,
            headHeight = headHeight,
            barHeight = barHeight,
            arrowRotated = arrowRotated
        )
    }
}

@Composable
private fun ReadProgressIndicator(
    progress: MutableFloatState,
    headHeight: MutableFloatState,
    barHeight: MutableFloatState,
    arrowRotated: MutableState<Boolean>
) {
    LaunchedEffect(key1 = progress.floatValue) {
        if (progress.floatValue >= 0.5) {
            arrowRotated.value = true
        } else {
            arrowRotated.value = false
        }
    }

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerSize.width
    val screenHeight = windowInfo.containerSize.height

    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp),
        color = if (headHeight.floatValue - barHeight.floatValue <= screenHeight || progress.floatValue >= 1) {
            MaterialTheme.colorScheme.surface
        } else {
            ProgressIndicatorDefaults.linearColor
        },
        trackColor = Color.Transparent,
        strokeCap = StrokeCap.Square,
        gapSize = 0.dp,
        progress = {
            if (headHeight.floatValue - barHeight.floatValue <= screenHeight) {
                1f
            } else {
                progress.floatValue
            }
        },
        drawStopIndicator = { }
    )
}

@Composable
private fun BodyContent(
    data: ThreadDetailEntity?,
    viewModel: PostViewModel,
    listState: LazyListState,
    showSnapshot: MutableState<Boolean>,
    openCreatePostScreen: MutableState<Boolean>,
    currentCreatePostData: MutableState<CreatePostEntity>,
    stickyLayoutController: State<StickyLayoutController>,
    targetPid: Int?,
    onTabSelected: (Int, ScrollableState) -> Unit = { _, _ -> }
) {
    if (data == null) {
        return
    }

    val scope = rememberCoroutineScope()
    val repliesData by viewModel.repliesData.collectAsStateWithLifecycle()
    val threadDetailData by viewModel.threadDetailData.collectAsStateWithLifecycle()
    val sortType = remember { mutableStateOf<ReplySortType?>(ReplySortType.DEFAULT) }
    val currentReplyAuthorId = rememberSaveable { mutableStateOf("") }
    val currentReplyAuthorName = rememberSaveable { mutableStateOf("") }

    //要是超过15页就不寻找了
    val showFindPidDialog = rememberSaveable { mutableStateOf(targetPid.toIntOrElse() > 0 && data.total.toIntOrElse() <= 20 * 15) }

    fun loadMore() {
        viewModel.getReplies(
            loadMore = true,
            init = false,
            threadId = data.thread?.threadId.toString(),
            authorId = when (sortType.value) {
                ReplySortType.AUTHOR -> {
                    data.thread?.authorId.toString()
                }
                ReplySortType.REPLY_AUTHOR -> {
                    currentReplyAuthorId.value
                }
                else -> {
                    null
                }
            },
            order = if (sortType.value == ReplySortType.NEW) "1" else "2"
        )
    }

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { index ->
            viewModel.setFirstVisibleIndex(index)
        }
    }

    LaunchedEffect(repliesData.data?.size) {
        snapshotFlow {
            repliesData.data
        }.collect {
            if (showFindPidDialog.value) {
                val value = it?.find { it.postId == targetPid }
                if (value == null) {
                    if (repliesData.hasMore) {
                        loadMore()
                        XLog.tag(TAG).d("not find target pid, auto load more")
                    } else {
                        showFindPidDialog.value = false
                        XLog.tag(TAG).d("not find target pid, no more data")
                    }
                } else {
                    scope.launchSafety {
                        delay(1000)
                        stickyLayoutController.value.scrollToBody?.invoke(500)
                        listState.scrollToItem(value.position.toIntOrElse(1) - 2)
                        delay(200)
                        showFindPidDialog.value = false
                        value.highLight.value = true
                    }
                    XLog.tag(TAG).d("find target pid, position = ${value.position}")
                }
            } else {
                XLog.tag(TAG).d("do not find target pid, targetPid = ${targetPid}, total size = ${data.total}")
            }
        }
    }

    LaunchedEffect(Unit) {
        onTabSelected(0, listState)
    }

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        if (showSnapshot.value.not()) {
            ReplyTitle(
                viewModel = viewModel,
                data = data,
                currentReplyAuthorId = currentReplyAuthorId,
                currentReplyAuthorName = currentReplyAuthorName,
                sortType = sortType,
                onFilterChange = {
                    sortType.value = it
                }
            )
        }

        SwipeRefresh(
            modifier = Modifier.fillMaxHeight(),
            uiState = repliesData,
            listState = listState,
            enableRefresh = false,
            showNoMoreMsg = false,
            showEmptyRetryBtn = false,
            dataEmptyContentOffset = (-200).dp,
            key = { _, item -> item.postId },
            onLoadMore = {
                loadMore()
            },
            onRetry = {
                viewModel.getReplies(
                    loadMore = it == RetryType.LoadMore,
                    init = it == RetryType.Init,
                    threadId = data.thread?.threadId.toString(),
                    authorId = when (sortType.value) {
                        ReplySortType.AUTHOR -> {
                            data.thread?.authorId.toString()
                        }
                        ReplySortType.REPLY_AUTHOR -> {
                            currentReplyAuthorId.value
                        }
                        else -> {
                            null
                        }
                    },
                    order = if (sortType.value == ReplySortType.NEW) "1" else "2"
                )
            }
        ) { index, item ->
            ThreadReplyItem(
                modifier = Modifier
                    .animateItem(),
                data = data,
                item = item,
                index = index,
                viewModel = viewModel,
                showSnapshot = showSnapshot,
                filerAuthorClick = { id, name ->
                    if (id == data.thread?.authorId.toString()) {
                        sortType.value = ReplySortType.AUTHOR
                        currentReplyAuthorId.value = ""
                        viewModel.getReplies(
                            loadMore = false,
                            init = true,
                            threadId = data.thread?.threadId.toString(),
                            authorId = data.thread?.authorId.toString(),
                            order = "2"
                        )
                    } else {
                        sortType.value = ReplySortType.REPLY_AUTHOR
                        currentReplyAuthorId.value = id.toString()
                        currentReplyAuthorName.value = name.toString()
                        viewModel.getReplies(
                            loadMore = false,
                            init = true,
                            threadId = data.thread?.threadId.toString(),
                            authorId = currentReplyAuthorId.value,
                            order = "2"
                        )
                    }
                },
                onDeleteSuccess = { pid ->
                    repliesData.data?.removeIf { it.postId.toString() == pid }
                },
                onCreatePost = {
                    currentCreatePostData.value = CreatePostEntity(
                        threadId = item.threadId.toIntOrElse(),
                        postId = item.postId.toIntOrElse(),
                        replyName = item.author.toString(),
                        allowAnonymous = data.forum?.canPostAnonymously == true
                    )
                    openCreatePostScreen.value = true
                }
            )
        }
    }

    LoadingDialog(
        showDialog = showFindPidDialog.value,
        cancelable = false,
        text = stringResource(R.string.please_wait)
    ) {
        showFindPidDialog.value = false
    }
}

@Composable
private fun ReplyTitle(
    viewModel: PostViewModel,
    data: ThreadDetailEntity?,
    currentReplyAuthorId: MutableState<String>,
    currentReplyAuthorName: MutableState<String>,
    sortType: MutableState<ReplySortType?>,
    onFilterChange: (sort: ReplySortType?) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val replyCount by viewModel.currentReplyCount.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val titleDefault = stringResource(id = R.string.default_)
    val titleNew = stringResource(id = R.string.newest)
    val titleAuthor = stringResource(id = R.string.author)

    val titles = remember(data?.thread?.authorId.toIntOrElse()) {
        mutableStateListOf(
            Pair(titleDefault, ReplySortType.DEFAULT),
            Pair(titleNew, ReplySortType.NEW),
        ).apply {
            if (data?.thread?.authorId.toIntOrElse() != 0) {
                add(Pair(titleAuthor, ReplySortType.AUTHOR))
            }
        }
    }

    LaunchedEffect(currentReplyAuthorId.value) {
        if (currentReplyAuthorId.value.isNotNullAndEmpty()) {
            scope.launchSafety {
                scrollState.animateScrollBy(1000f)
            }
        }
    }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        Text(
            text = stringResource(R.string.thread_detail_replies_title, replyCount),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(20.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .horizontalScroll(scrollState)
        ) {
            titles.forEachIndexed { index, pair ->
                FilterChip(
                    label = {
                        Text(
                            text = pair.first,
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(end = if (index < titles.size - 1) 10.dp else 0.dp),
                    border = null,
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    selected = sortType.value == pair.second,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentReplyAuthorId.value = ""
                        sortType.value = pair.second
                        onFilterChange.invoke(sortType.value)
                        viewModel.getReplies(
                            loadMore = false,
                            init = true,
                            threadId = data?.thread?.threadId.toString(),
                            authorId = if (sortType.value == ReplySortType.AUTHOR) data?.thread?.authorId.toString() else null,
                            order = if (sortType.value == ReplySortType.NEW) "1" else "2"
                        )
                    }
                )
            }

            AnimatedVisibility(
                visible = currentReplyAuthorId.value.isNotNullAndEmpty()
            ) {
                FilterChip(
                    label = {
                        Text(
                            text = currentReplyAuthorName.value,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .padding(start =  10.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = null,
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    selected = true,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        sortType.value = ReplySortType.REPLY_AUTHOR
                        onFilterChange.invoke(sortType.value)
                        viewModel.getReplies(
                            loadMore = false,
                            init = true,
                            threadId = data?.thread?.threadId.toString(),
                            authorId = currentReplyAuthorId.value,
                            order = if (sortType.value == ReplySortType.NEW) "1" else "2"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun Content(
    data: ThreadDetailEntity?,
    showSnapshot: MutableState<Boolean>,
    viewModel: PostViewModel
) {
    if (data == null) {
        return
    }

    val context = LocalContext.current
    val navHostController = LocalNavController.current

    val row = remember { mutableStateOf(data.rows?.getOrNull(0)) }
    val typeName = rememberSaveable { mutableStateOf(data.forum?.threadTypes?.find { it.typeId == data.thread?.typeId }?.name) }

    //TODO: format =2：markdown  ,  format=-1：message不包含附件，附件需单独处理，  0：bbcode message内容里也可能没有图片，比如2155653
    LocalHtmlWebView(
        content = row.value?.message,
        format = row.value?.format,
        enableLongClick = showSnapshot.value.not(),
        uniqueId = row.value?.postId.toString(),
        attachments = row.value?.attachments,
        defaultFontSize = 16,
        onPageFinished = {
            viewModel.webViewReady()
        }
    )

    Spacer(modifier = Modifier.height(pagePadding))

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(cardCorner)
            )
            .clip(
                shape = RoundedCornerShape(cardCorner)
            )
            .clickable(unbound = false) {
                navHostController.navigate(Router.ForumDetailRouterEntity(
                    fid = data.forum?.fid.toIntOrElse()
                ))
            }
            .padding(horizontal = 5.dp)
    ) {
        AsyncImage(
            model = ForumPicture[ForumCategoryManager.getSecondaryRootForum(data.forum?.fid)?.fid],
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(15.dp)
                .clip(shape = RoundedCornerShape(50))
        )

        Text(
            text = data.forum?.name.toString().plus(
                if (typeName.value.isNotNullAndEmpty()) "-${typeName.value}" else ""
            ),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun ThreadInfo(
    data: ThreadDetailEntity?,
    showSnapshot: MutableState<Boolean>,
    viewModel: PostViewModel
) {
    if (data == null) {
        return
    }

    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val row = remember { mutableStateOf(data.rows?.getOrNull(0)) }

    if (showSnapshot.value) {
        Text(
            text = stringResource(R.string.snapshot_data_show_dsp),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(3.dp)
                )
                .padding(vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
    }

    if (Constants.INTERNAL_FIDS.contains(data.thread?.forumId ?: 0)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(horizontal = 40.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.thread_detail_confidential).toUpperCase(Locale.current),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    if (row.value?.warned == true) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(vertical = 15.dp)
        ) {
            IconTitle(
                icon = Icons.Outlined.WarningAmber,
                iconTint = MaterialTheme.colorScheme.error,
                gap = 2.dp,
                iconSize = 18.dp,
                text = "本帖被管理员或版主警告",
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    CommonThreadTitle(
        data = data.thread,
        labelTextStyle = TextStyle(
            fontSize = 12.sp
        ),
        textStyle = TextStyle(
            fontSize = 20.sp
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .horizontalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = row.value?.authorId.toAvatarUrl(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(shape = RoundedCornerShape(50))
                .clickable(enabled = true) {
                    navHostController.navigate(
                        Router.UserProfileRouterEntity(
                            uid = data.thread?.authorId,
                            name = data.thread?.author.toString()
                        )
                    )
                }
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = row.value?.author.toString(),
            fontSize = 14.sp,
            lineHeight = 14.sp,
            modifier = Modifier
                .unboundClickable {
                    navHostController.navigate(
                        Router.UserProfileRouterEntity(
                            uid = data.thread?.authorId,
                            name = data.thread?.author.toString()
                        )
                    )
                }
                .alpha(alpha = 0.7f)
        )

        if (row.value?.authorDetails != null) {
            val outlineTitle = if (row.value?.authorDetails?.groupTitle.isNotNullAndEmpty()
                && row.value?.authorDetails?.groupTitle?.contains("Lv") == false) {
                row.value?.authorDetails?.groupTitle
            } else if (row.value?.authorDetails?.groupSubtitle.isNotNullAndEmpty()) {
                row.value?.authorDetails?.groupSubtitle
            } else {
                ""
            }
            if (outlineTitle.isNotNullAndEmpty()) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = outlineTitle,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .alpha(0.5f)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            Text(
                text = " · Lv${row.value?.authorDetails?.levelId}",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )
        }

        Text(
            text = " · ${formatTimestamp(data.thread?.dateline, LocalContext.current)}",
            fontSize = 14.sp,
            lineHeight = 14.sp,
            modifier = Modifier.alpha(0.7f)
        )

        if (!row.value?.authorDetails?.medals.isNullOrEmpty()) {
            Text(
                text = " · ",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                modifier = Modifier.alpha(0.7f)
            )

            Row {
                row.value?.authorDetails?.medals?.forEachIndexed { index, i ->
                    viewModel.postRepository.dataBase.getMedalDao().findFirstById(i)?.let {
                        AsyncImage(
                            model = it.image,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }

    if (data.thread?.replyCredit != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .background(
                    color = LocalCustomColors.current.threadDetailReplyAward.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(pagePadding)
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.thread_detail_reply_award_title),
                    fontSize = 18.sp,
                    color = LocalCustomColors.current.threadDetailReplyAward
                )
                Text(
                    text = if (data.thread.replyCredit.probability.toIntOrElse() in 1 until  100) {
                        stringResource(id = R.string.thread_detail_reply_award_dsp_possibility,
                            data.thread.replyCredit.creditAmount.toString(),
                            data.thread.replyCredit.creditName.toString(),
                            data.thread.replyCredit.limitPerUser.toString(),
                            data.thread.replyCredit.probability.toString(),
                            data.thread.replyCredit.remainingAmount.toString())
                    } else {
                        stringResource(id = R.string.thread_detail_reply_award_dsp,
                            data.thread.replyCredit.creditAmount.toString(),
                            data.thread.replyCredit.creditName.toString(),
                            data.thread.replyCredit.limitPerUser.toString(),
                            data.thread.replyCredit.remainingAmount.toString())
                    },
                    fontSize = 14.sp,
                    color = LocalCustomColors.current.threadDetailReplyAward,
                    modifier = Modifier.padding(end = 80.dp)
                )
            }
            Icon(
                imageVector = Icons.Outlined.CardGiftcard,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(60.dp),
                tint = LocalCustomColors.current.threadDetailReplyAward.copy(alpha = 0.2f),
                contentDescription = null
            )
        }
    }

    if (data.thread?.rushReply != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .background(
                    color = LocalCustomColors.current.threadDetailReplyAward.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(pagePadding)
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.thread_detail_reply_award_title),
                    fontSize = 18.sp,
                    color = LocalCustomColors.current.threadDetailReplyAward
                )
                Text(
                    text = stringResource(id = R.string.thread_detail_rush_reply_dsp,
                        data.thread.rushReply.creditLimit.toString(),
                        formatTimestampYMDHMS(data.thread.rushReply.startTime.toIntOrElse(), context),
                        formatTimestampYMDHMS(data.thread.rushReply.endTime.toIntOrElse(), context),
                        data.thread.rushReply.maxPosition.toString(),
                        data.thread.rushReply.targetPositions.toString()
                    ),
                    fontSize = 14.sp,
                    color = LocalCustomColors.current.threadDetailReplyAward,
                    modifier = Modifier.padding(end = 80.dp)
                )
            }
            Icon(
                imageVector = Icons.Outlined.CardGiftcard,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(60.dp),
                tint = LocalCustomColors.current.threadDetailReplyAward.copy(alpha = 0.2f),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun PollInfo(
    data: ThreadDetailEntity?,
    viewModel: PostViewModel,
    showSnapshot: MutableState<Boolean>,
) {
    if (data?.thread?.poll == null) {
        return
    }

    val scope = rememberCoroutineScope()
    val voteRequestData by viewModel.voteData.collectAsStateWithLifecycle()
    val pollData = rememberSaveable { mutableStateOf(if (voteRequestData.data != null) voteRequestData.data else data.thread.poll) }
    val closed by remember {
        derivedStateOf {
            mutableStateOf(System.currentTimeMillis() / 1000 > pollData.value?.expiration.toString().toLongOrDefault(0)
                    && pollData.value?.expiration.toString().toLongOrDefault(0) > 0
            )
        }
    }

    val voted by remember {
        derivedStateOf {
            mutableStateOf(pollData.value?.selected.isNotNullAndEmpty())
        }
    }

    val selectedOptions = rememberSaveable { mutableStateOf<List<Int>>(mutableListOf()) }

    LaunchedEffect(voteRequestData.data) {
        if (voteRequestData.data != null) {
            data.thread.poll = voteRequestData.data
            pollData.value = voteRequestData.data
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cardCorner * 2)
            )
            .padding(15.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconTitle(
                icon = Icons.Outlined.Poll,
                iconSize = 22.dp,
                text = stringResource(id = R.string.poll),
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            )

            Text(
                text = if (voted.value) {
                    stringResource(
                        R.string.thread_detail_polled,
                        pollData.value?.voterCount.toString()
                    )
                } else if (closed.value) {
                    stringResource(
                        R.string.thread_detail_poll_close,
                        pollData.value?.voterCount.toString()
                    )
                } else {
                    stringResource(
                        R.string.thread_detail_poll_ing,
                        pollData.value?.maxChoices.toString(),
                        pollData.value?.voterCount.toString()
                    )
                },
                fontSize = 14.sp,
                modifier = Modifier
                    .alpha(0.6f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            pollData.value?.options?.forEachIndexed { index, option ->
                var percent by remember { mutableFloatStateOf(0f) }
                val isChecked = selectedOptions.value.contains(option.id ?: 0)
                val enable = (isChecked || ((pollData.value?.maxChoices ?: 0) > selectedOptions.value.size)) && !voted.value && !closed.value

                scope.launchSafety {
                    delay(100)
                    percent = if(pollData.value?.voterCount == 0) {
                        0f
                    } else {
                        (option.votes?.toFloat() ?: 0f) / (pollData.value?.voterCount?.toFloat() ?: 1f)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(5.dp)
                        )
                        .clickable(enabled = true) {
                            if (enable && !showSnapshot.value) {
                                selectedOptions.value = if (isChecked) {
                                    (selectedOptions.value - ((option.id ?: 0)))
                                } else if ((pollData.value?.maxChoices ?: 0) > selectedOptions.value.size) {
                                    (selectedOptions.value + ((option.id ?: 0)))
                                } else {
                                    selectedOptions.value
                                }
                            }
                        }
                        .alpha(
                            animateFloatAsState(
                                targetValue = if (enable) 1f else 0.4f,
                                animationSpec = tween(durationMillis = 200),
                                label = "enable_alpha"
                            ).value
                        )
                ) {
                    if (pollData.value?.visible == true || voted.value || closed.value) {
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(
                                fraction = animateFloatAsState(
                                    targetValue = percent,
                                    animationSpec = tween(durationMillis = 2000),
                                    label = "progress"
                                ).value
                            )
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = if (percent < 1f) {
                                    RoundedCornerShape(
                                        topStart = cardCorner,
                                        bottomStart = cardCorner
                                    )
                                } else {
                                    RoundedCornerShape(cardCorner)
                                }
                            )
                        )
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = pagePadding)
                            .padding(vertical = 8.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            if (voted.value || !closed.value) {
                                RoundCheckBox(
                                    isChecked = if (voted.value) {
                                        pollData.value?.selected?.contains(option.id ?: 0) == true
                                    } else {
                                        selectedOptions.value.contains(option.id ?: 0)
                                    },
                                    radius = 8.dp,
                                    onClick = null,
                                    color = RoundCheckBoxDefaults.colors(
                                        borderColor = MaterialTheme.colorScheme.primary,
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.padding(0.dp)
                                )
                            }

                            Text(
                                text = option.text.toString(),
                                fontSize = 15.sp,
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        if (pollData.value?.visible == true || voted.value || closed.value) {
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%.1f", percent * 100)
                                    .plus("% ")
                                    .plus("(${option.votes.toString()})"),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }
                    }

//                    if (option.voters.isNotNullAndEmpty()) {
//                        LazyRow (
//                            modifier = Modifier
//                                .height(20.dp)
//                                .align(Alignment.BottomStart)
//                        ) {
//                            itemsIndexed(option.voters) { index, item ->
//                                AsyncImage(
//                                    model = item.toAvatarUrl(),
//                                    contentDescription = null,
//                                    contentScale = ContentScale.Crop,
//                                    modifier = Modifier
//                                        .size(15.dp)
//                                        .clip(RoundedCornerShape(50))
//                                )
//                            }
//                        }
//                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedOptions.value.isNotEmpty() && !voted.value && !closed.value && !showSnapshot.value,
            enter = fadeIn() + expandIn(expandFrom = Alignment.TopCenter),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.TopCenter)
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        XLog.tag(TAG).d(selectedOptions.value)
                        viewModel.vote(
                            VoteRequestEntity(
                                threadId = data.thread.threadId ?: 0,
                                options = selectedOptions.value
                            )
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.poll)
                    )
                }
            }
        }
    }
}

@Composable
private fun Collection(
    data: ThreadDetailEntity?,
) {
    if (data?.thread?.collections.isNullOrEmpty()) {
        return
    }

    val navHostController = LocalNavController.current

    Spacer(modifier = Modifier.height(10.dp))

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cardCorner * 2)
            )
            .padding(15.dp)
    ) {
        IconTitle(
            icon = Icons.Outlined.CollectionsBookmark,
            iconSize = 20.dp,
            text = stringResource(id = R.string.collection),
            textStyle = TextStyle(
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        Text(
            text = stringResource(id = R.string.thread_detail_collection_dsp),
            fontSize = 14.sp,
            modifier = Modifier
                .alpha(0.6f)
        )

        data?.thread?.collections?.fastForEachIndexed { i, collectionEntity ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(cardCorner)
                    )
                    .clip(shape = RoundedCornerShape(cardCorner))
                    .clickable(enabled = true){
                        navHostController.navigate(Router.CollectionDetailRouterEntity(collectionEntity.collectionId.toIntOrElse()))
                    }
                    .padding(pagePadding + 5.dp)
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
                            .weight(1f, false)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    IconTitle(
                        icon = Icons.Outlined.BookmarkAdd,
                        iconSize = 22.dp,
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconPosition = IconPosition.RIGHT,
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
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
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shape = RoundedCornerShape(2.dp)
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
                        model = collectionEntity.uid.toAvatarUrl(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(15.dp)
                            .clip(shape = RoundedCornerShape(50))
                            .clickable(enabled = true) {
                                navHostController.navigate(
                                    Router.UserProfileRouterEntity(
                                        uid = collectionEntity.uid,
                                        name = collectionEntity.username.toString()
                                    )
                                )
                            }
                    )

                    Text(
                        text = collectionEntity.username.toString(),
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
                        text = stringResource(id = R.string.thread_detail_collection_count, collectionEntity.threads.toString()),
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentInfo(
    data: PostCommentAndRateEntity?,
    threadId: String?,
    postId: String?
) {
    val navHostController = LocalNavController.current
    if (data != null && data.comments.isNotEmpty()) {
        val hasMore = data.commentTotal.toIntOrElse() > 5
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(10.dp))

        Column (
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(cardCorner * 2)
                )
                .padding(15.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            ) {
                IconTitle(
                    icon = Icons.Outlined.RateReview,
                    iconSize = 20.dp,
                    text = stringResource(id = R.string.comment)
                        .plus("(${data.commentTotal.toString()})"),
                    textStyle = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )

                if (hasMore) {
                    Text(
                        text = stringResource(R.string.view_more),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .unboundClickable {
                                navHostController.navigate(Router.PostCommentAndRateRouterEntity(
                                    tid = threadId.toIntOrElse(),
                                    pid = postId.toIntOrElse(),
                                    tab = CommentRateType.COMMENT.name
                                ))
                            }
                    )
                }
            }

            data.comments
                .subList(0, minOf(5, data.comments.size))
                .forEach {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            AsyncImage(
                                model = it.authorId.toAvatarUrl(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(shape = RoundedCornerShape(50))
                            )
                            Text(
                                text = it.author.toString(),
                                fontSize = 15.sp,
                                modifier = Modifier.alpha(alpha = 0.5f)
                            )
                            Text(
                                text = formatTimestamp(it.dateline, context),
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .alpha(alpha = 0.5f)
                            )
                        }
                        Text(
                            text = it.message.toString(),
                            fontSize = 15.sp,
                            modifier = Modifier
                                .alpha(alpha = 0.8f)
                        )
                    }
                }
        }
    }
}

@Composable
fun RateInfo(
    data: PostCommentAndRateEntity?,
    threadId: String?,
    postId: String?
) {
    val navHostController = LocalNavController.current
    if (data == null || data.rates.isEmpty()) {
        return
    }
    val hasMore = remember { data.rates.size.toIntOrElse() > 5 }
    val waterTotalSize = remember { data.rateStat.totalCredits.water.toIntOrElse() }
    val weiWangTotalSize = remember { data.rateStat.totalCredits.weiWang.toIntOrElse() }

    val context = LocalContext.current

    Spacer(modifier = Modifier.height(10.dp))

    Column (
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                shape = RoundedCornerShape(cardCorner * 2)
            )
            .padding(15.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
        ) {
            IconTitle(
                icon = Icons.Outlined.StarOutline,
                iconSize = 20.dp,
                text = stringResource(id = R.string.rate)
                    .plus("(${data.rates.size})"),
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            )

            if (hasMore) {
                Text(
                    text = stringResource(R.string.view_more),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .unboundClickable {
                            navHostController.navigate(Router.PostCommentAndRateRouterEntity(
                                tid = threadId.toIntOrElse(),
                                pid = postId.toIntOrElse(),
                                tab = CommentRateType.RATE.name
                            ))
                        }
                )
            }
        }

        Text(
            text = stringResource(R.string.thread_detail_rate_dsp, data.rateStat.totalUsers.toIntOrElse(),)
                .plus(" ")
                .plus(if (waterTotalSize != 0) stringResource(R.string.water).plus("${if (waterTotalSize > 0 ) "+" else ""}${waterTotalSize} ") else "")
                .plus(if (weiWangTotalSize != 0) stringResource(R.string.prestige).plus("${if (weiWangTotalSize > 0 ) "+" else ""}${weiWangTotalSize} ") else ""),
            fontSize = 14.sp,
            modifier = Modifier
                .alpha(0.6f)
        )

        data.rates
            .subList(0, minOf(5, data.rates.size))
            .forEach {
                val waterSize by remember { mutableIntStateOf(it.credits?.water.toIntOrElse()) }
                val weiWangSize by remember { mutableIntStateOf(it.credits?.weiWang.toIntOrElse()) }

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            AsyncImage(
                                model = it.userId.toAvatarUrl(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(shape = RoundedCornerShape(50))
                                    .clickable(unbound = true) {
                                        navHostController.navigate(
                                            Router.UserProfileRouterEntity(
                                                uid = it.userId,
                                                name = it.username.toString()
                                            )
                                        )
                                    }
                            )
                            Text(
                                text = it.username.toString(),
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .alpha(alpha = 0.5f)
                                    .clickable(unbound = true) {
                                        navHostController.navigate(
                                            Router.UserProfileRouterEntity(
                                                uid = it.userId,
                                                name = it.username.toString()
                                            )
                                        )
                                    }
                            )
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (waterSize != 0) {
                                Text(
                                    text = stringResource(R.string.water)
                                        .plus("${if (waterSize > 0 ) "+" else ""}${waterSize}"),
                                    fontSize = 11.sp,
                                    lineHeight = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(
                                            color = if (waterSize > 0) {
                                                LocalCustomColors.current.waterDrop
                                            } else {
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                            },
                                            shape = RoundedCornerShape(cardCorner)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                )
                            }
                            if (weiWangSize != 0) {
                                Text(
                                    text = stringResource(R.string.prestige)
                                        .plus("${if (weiWangSize > 0 ) "+" else ""}${weiWangSize}"),
                                    fontSize = 11.sp,
                                    lineHeight = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(
                                            color = if (waterSize > 0) {
                                                LocalCustomColors.current.prestige
                                            } else {
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                            },
                                            shape = RoundedCornerShape(cardCorner)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    if (it.reason.isNotNullAndEmpty()) {
                        Text(
                            text = it.reason.toString(),
                            fontSize = 15.sp,
                            modifier = Modifier.alpha(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = formatTimestamp(it.dateline, context),
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(alpha = 0.5f)
                    )
                }
            }
    }
}

@Composable
private fun SupportInfo(
    data: ThreadDetailEntity?,
    viewModel: PostViewModel,
    showSnapshot: MutableState<Boolean>,
) {
    if (data == null) {
        return
    }

    val row = remember { mutableStateOf(data.rows?.getOrNull(0)) }

    val supportData by viewModel.supportData(row.value?.postId.toString()).collectAsStateWithLifecycle()
    val supportCount = rememberSaveable { mutableIntStateOf(data.thread?.recommendAdd.toIntOrElse()) }
    val againstCount = rememberSaveable { mutableIntStateOf(data.thread?.recommendSub.toIntOrElse()) }

    LaunchedEffect(supportData.data) {
        if (supportData.data != null && supportData.data?.success == true) {
            if (supportData.data?.support == true) {
                supportCount.intValue += 1
            } else {
                againstCount.intValue += 1
            }
        }
    }

    LikeDislikeProgressBar(
        leftNum = againstCount.intValue,
        rightNum = supportCount.intValue,
        progress = 1f,
        colors = listOf(
            LocalCustomColors.current.threadDislikeProgress,
            LocalCustomColors.current.threadDislikeProgress,
            LocalCustomColors.current.threadLikeProgress,
            LocalCustomColors.current.threadLikeProgress
        ),
        iconSize = 25.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        onLeftClick = {
            if (!showSnapshot.value) {
                viewModel.support(
                    tid = data.thread?.threadId.toString(),
                    pid = row.value?.postId.toString(),
                    support = false
                )
            }
        },
        onRightClick = {
            if (!showSnapshot.value) {
                viewModel.support(
                    tid = data.thread?.threadId.toString(),
                    pid = row.value?.postId.toString(),
                    support = true
                )
            }
        }
    )
}

@Composable
private fun BottomBar(
    data: ThreadDetailEntity?,
    viewModel: PostViewModel,
    openCommentBottomSheet: MutableState<Boolean>,
    openRateBottomSheet: MutableState<Boolean>,
    scrollDirection: MutableState<Int?>,
    openCreatePostScreen: MutableState<Boolean>,
    currentCreatePostData: MutableState<CreatePostEntity>,
    modifier: Modifier
) {
    val context = LocalContext.current
    val isFavorite = remember { mutableStateOf(data?.thread?.isFavorite == true) }
    val favoriteCount = remember { mutableIntStateOf(data?.thread?.favoriteTimes.toIntOrElse()) }
    val favoriteData = viewModel.threadFavoriteData.collectAsStateWithLifecycle()

    LaunchedEffect(favoriteData.value) {
        if (favoriteData.value.data != null) {
            if (favoriteData.value.isSuccess) {
                isFavorite.value = favoriteData.value.data!!
                if (isFavorite.value) {
                    favoriteCount.intValue += 1
                    ContextCompat.getString(context, R.string.thread_detail_favorite_success).showToast(context)
                } else {
                    favoriteCount.intValue -= 1
                    ContextCompat.getString(context, R.string.thread_detail_favorite_del_success).showToast(context)
                }
            } else {
                ContextCompat.getString(context, R.string.operation_fail).showToast(context)
            }
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = (scrollDirection.value ?: 1) > 0,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = WindowInsets.navigationBars.getBottom(LocalDensity.current).px2dp)
                .padding(20.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(50)
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 15.dp
                )
                .clickable(
                    interactionSource = null,
                    indication = null,
                ) { }
        ) {
            IconTitle(
                icon = Icons.Outlined.EditNote,
                iconSize = 24.dp,
                text = "发评论",
                gap = 2.dp,
                textStyle = TextStyle(
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .unboundClickable {
                        currentCreatePostData.value = CreatePostEntity(
                            threadId = data?.thread?.threadId.toIntOrElse(),
                            postId = data?.thread?.postId.toIntOrElse(),
                            replyName = data?.thread?.author.toString(),
                            allowAnonymous = data?.forum?.canPostAnonymously == true
                        )
                        openCreatePostScreen.value = true
                    }
            )

            VerticalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier
                    .height(10.dp)
                    .padding(horizontal = 10.dp)
            )

            Text(
                text = "点评",
                fontSize = 14.sp,
                modifier = Modifier
                    .unboundClickable {
                        openCommentBottomSheet.value = true
                    }
            )

            Spacer(modifier = Modifier.width(40.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                BadgedBox (
                    badge = {
                        Badge (
                            containerColor = Color.Transparent,
                            modifier = Modifier.offset(y = (-4).dp)
                        ) {
                            Text(
                                text = if (data?.rows?.getOrNull(0)?.commentAndRate?.rates?.size.toIntOrElse() > 0) {
                                    data?.rows?.getOrNull(0)?.commentAndRate?.rates?.size.toIntOrElse().toString()
                                } else {
                                    ""
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .unboundClickable {
                            openRateBottomSheet.value = true
                        }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null
                    )
                }

                BadgedBox (
                    badge = {
                        Badge (
                            containerColor = Color.Transparent,
                            modifier = Modifier.offset(y = (-4).dp)
                        ) {
                            Text(
                                text = if (favoriteCount.intValue > 0) {
                                    favoriteCount.intValue.toString()
                                } else {
                                    ""
                                },
                                color = if (isFavorite.value) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .unboundClickable {
                            if (isFavorite.value) {
                                viewModel.delFavorite(data?.thread?.threadId.toString())
                            } else {
                                viewModel.favorite(data?.thread?.threadId.toString())
                            }
                        }
                ) {
                    Icon(
                        imageVector = if (isFavorite.value) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.StarOutline
                        },
                        tint = if (isFavorite.value) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptions(
    sheetState: SheetState,
    openMoreOptionsBottomSheet: MutableState<Boolean>,
    openReportBottomSheet: MutableState<Boolean>,
    data: ThreadDetailEntity?,
    viewModel: PostViewModel,
    showSnapshot: MutableState<Boolean>,
    headGraphicsLayer: GraphicsLayer
) {
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val snapshotViewModel: SnapshotViewModel = hiltViewModel()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val showSnapshotDialog = rememberSaveable { mutableStateOf(false) }
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val threadDetailData by viewModel.threadDetailData.collectAsStateWithLifecycle()
    val self = rememberSaveable { mutableStateOf(AccountManager.getSignedInAccount()?.uid == data?.thread?.authorId.toString()) }
    val row = remember { mutableStateOf(data?.rows?.getOrNull(0)) }
    val addToCollectionBottomSheet = rememberSaveable { mutableStateOf(false) }
    val addToCollectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = openMoreOptionsBottomSheet.value) {
        scope.launchSafety {
            sheetState.hide()
        }
    }

    fun hide() {
        scope.launchSafety {
            sheetState.hide()
            openMoreOptionsBottomSheet.value = false
        }
    }

    if (openMoreOptionsBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openMoreOptionsBottomSheet.value = false
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
                //todo 内容太长时，截出的内容不完整，或者崩溃。。。和webView有关，有时间再看
//                item {
//                    MoreOptionItem(
//                        text = "截取内容",
//                        icon = Icons.Outlined.ContentCut,
//                        enable = !Constants.INTERNAL_FIDS.contains(data?.thread?.forumId ?: 0)
//                    ) {
//                        scope.launchSafety {
//                            withContext(Dispatchers.IO) {
//                                val bitmap = headGraphicsLayer.toImageBitmap().asAndroidBitmap()
//                                ImageSaveUtil.saveToAlbum(bitmap, context)
//                            }
//                            sheetState.hide()
//                            openSheet.value = false
//                        }
//                    }
//                }

                if (showSnapshot.value.not()) {
                    item {
                        MoreOptionItem(
                            text = stringResource(id = R.string.add_to_collection),
                            icon = Icons.Outlined.LibraryAdd
                        ) {
                            hide()
                            addToCollectionBottomSheet.value = true
                        }
                    }
                }

                if (self.value && showSnapshot.value.not()) {
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

                if (showSnapshot.value.not() && Constants.INTERNAL_FIDS.contains(data?.thread?.forumId ?: 0).not()) {
                    item {
                        MoreOptionItem(
                            text = stringResource(R.string.snapshot_title),
                            icon = Icons.Outlined.PhotoCamera
                        ) {
                            showSnapshotDialog.value = true
                        }
                    }
                }

                item {
                    MoreOptionItem(
                        text = stringResource(id = R.string.copy_link),
                        icon = Icons.Outlined.ContentCopy
                    ) {
                        hide()
                        Constants.BBS_URL.plus("/thread/${data?.thread?.threadId}").copyToClipBoard(context)
                    }
                }

                item {
                    MoreOptionItem(
                        text = stringResource(id = R.string.open_in_browser),
                        icon = Icons.Outlined.OpenInBrowser
                    ) {
                        hide()
                        uriHandler.openUri(Constants.BBS_URL.plus("/thread/${data?.thread?.threadId}"))
                    }
                }

                item {
                    MoreOptionItem(
                        text = stringResource(id = R.string.search_site),
                        icon = Icons.Outlined.Search
                    ) {
                        hide()
                        navHostController.navigate(Router.SearchRouterEntity)
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

    CommonConfirmDialog(
        showDialog = showSnapshotDialog,
        icon = Icons.Outlined.PhotoCamera,
        title = stringResource(R.string.snapshot_title),
        dsp = stringResource(R.string.snapshot_title_dsp),
        onConfirmClick = {
            showSnapshotDialog.value = false
            threadDetailData.data?.let {
                scope.launchSafety {
                    val success = snapshotViewModel.saveSnapshot(it)
                    if (success) {
                        openMoreOptionsBottomSheet.value = false
                        ContextCompat.getString(context, R.string.snapshot_save_success).showToast(context)
                    } else {
                        ContextCompat.getString(context, R.string.snapshot_save_fail).showToast(context)
                    }
                }
            }
        },
        onDismissRequest = {
            showSnapshotDialog.value = false
        }
    )

    if (showDeleteDialog.value) {
        DeletePostDialog(
            showDialog = showDeleteDialog,
            pid = row.value?.postId.toString(),
            tid = data?.thread?.threadId.toString(),
            viewModel = viewModel,
            onSuccess = {
                navHostController.popBackStack()
            }
        )
    }

    AddToCollectionBottomSheet(
        addToCollectionSheetState = addToCollectionSheetState,
        addToCollectionBottomSheet = addToCollectionBottomSheet,
        tid = data?.thread?.threadId.toIntOrElse()
    )
}

@Composable
fun MoreOptionItem(
    text: String,
    icon: ImageVector,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.alpha(if (enable) 1f else 0.5f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                )
                .unboundClickable {
                    if (enable) {
                        onClick.invoke()
                    }
                }
                .padding(15.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}

private enum class ReplySortType {
    DEFAULT, NEW, AUTHOR, REPLY_AUTHOR
}
