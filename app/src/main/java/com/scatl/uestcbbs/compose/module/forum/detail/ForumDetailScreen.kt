package com.scatl.uestcbbs.compose.module.forum.detail

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.google.android.renderscript.Toolkit
import com.scatl.uestcbbs.compose.ForumPicture
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.dp2px
import com.scatl.uestcbbs.compose.ext.isGTESdk31
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.lazyListSaver
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.module.forum.ForumViewModel
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.getBitmapFromUrl
import com.scatl.uestcbbs.compose.util.toArgb8888
import com.scatl.uestcbbs.compose.widget.ScrollableTabLayout
import com.scatl.uestcbbs.compose.widget.StatusLayout
import com.scatl.uestcbbs.compose.widget.StickyLayout
import com.scatl.uestcbbs.compose.widget.StickyLayoutController
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/7/24 10:11:22
 */
@Composable
fun ForumDetailScreen(
    fid: Int
) {
    val viewModel: ForumViewModel = hiltViewModel()
    val forumDetailData by viewModel.forumDetailData.collectAsStateWithLifecycle()
    val stickyLayoutController = rememberUpdatedState(remember { StickyLayoutController() })
    val progress = rememberSaveable { mutableFloatStateOf(0f) }
    val showFastUpBtn = rememberSaveable { mutableStateOf(false) }

    fun getFid(): Int {
        val forum = ForumCategoryManager.getSecondaryRootForum(fid.toIntOrElse())
        return forum?.fid?.toIntOrElse() ?: fid
    }

    LoadInitialDataIfNeeded(key = forumDetailData) {
        viewModel.getForumDetail(
            init = true,
            fid = getFid()
        )
    }

    StatusLayout(
        uiState = forumDetailData,
        onRetry = {
            viewModel.getForumDetail(
                init = true,
                fid = getFid()
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            StickyLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                controller = stickyLayoutController,
                parallaxCoefficient = 0.4f,
                bodyInitOffset = 20.dp.dp2px.toInt(),
                headContent = {
                    HeadContent(
                        data = forumDetailData.data,
                        progress = progress
                    )
                },
                barContent = {
                    BarContent(
                        data = forumDetailData.data,
                        progress = progress
                    )
                },
                bodyContent = {
                    BodyContent(
                        data = forumDetailData.data,
                        showFastUpBtn = showFastUpBtn,
                        viewModel = viewModel,
                        fid = fid,
                        onTabSelected = { index, state ->
                            stickyLayoutController.value.bodyStateChange?.invoke(state)
                        }
                    )
                },
                onProgress = { percent, offset ->
                    progress.floatValue = percent
                }
            )

            BottomBtn(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                showFastUpBtn = showFastUpBtn,
                stickyLayoutController = stickyLayoutController
            )
        }
    }
}

@Composable
private fun BottomBtn(
    modifier: Modifier = Modifier,
    showFastUpBtn: MutableState<Boolean>,
    stickyLayoutController: State<StickyLayoutController>,
) {
    val scope = rememberCoroutineScope()

    Column (
        modifier = modifier
            .padding(pagePadding * 2)
    ) {
        FloatingActionButton(
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            onClick = {

            }
        ) {
            Icon(imageVector = Icons.Outlined.Create, contentDescription = null)
        }

        AnimatedVisibility(visible = showFastUpBtn.value) {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    scope.launchSafety {
                        (stickyLayoutController.value.bodyState?.invoke() as? LazyListState)?.scrollToItem(0)
                    }
                }
            ) {
                Icon(imageVector = Icons.Outlined.KeyboardDoubleArrowUp, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarContent(
    data: ForumDetailEntity?,
    progress: MutableFloatState,
) {
    val navHostController = LocalNavController.current

    if (data == null) {
        return
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
        modifier = Modifier
            .background(color = Color.Transparent)
            .alpha(alpha = progress.floatValue * 3),
        title = {
            Text(
                text = data.name.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.content_dsp_forum_detail_back),
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(30.dp)
                    .unboundClickable {
                        navHostController.popBackStack()
                    }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HeadContent(
    data: ForumDetailEntity?,
    progress: MutableFloatState
) {
    if (data == null) {
        return
    }

    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val barHeight = TopAppBarDefaults.windowInsets.getTop(LocalDensity.current).px2dp + 64.dp
    val domainColor = rememberSaveable { mutableIntStateOf(0) }
    val forumImageBitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(ForumPicture[data.fid]) {
        forumImageBitmap.value = getBitmapFromUrl(context, ForumPicture[data.fid])
    }

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (forumImageBitmap.value != null) {
            AsyncImage(
                model = if (isGTESdk31()) forumImageBitmap.value else Toolkit.blur(forumImageBitmap.value!!.toArgb8888(), 25),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(150.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            )
        }

        Column (
            modifier = Modifier
                .padding(horizontal = pagePadding * 2)
                .alpha(1 - progress.floatValue)
        ) {
            Spacer(modifier = Modifier.height(barHeight))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ForumPicture[data.fid])
                        .allowHardware(false)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(cardCorner * 2)),
                    onState = {
                        if (it is AsyncImagePainter.State.Success && it.result.drawable is BitmapDrawable) {
                            val builder = Palette.from((it.result.drawable as BitmapDrawable).bitmap)
                            val palette = builder.generate()
                            if (palette.mutedSwatch != null) {
                                domainColor.intValue = palette.mutedSwatch!!.rgb
                            } else if (palette.vibrantSwatch != null) {
                                domainColor.intValue = palette.vibrantSwatch!!.rgb
                            }
                        }
                    }
                )

                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = data.name.toString(),
                        fontSize = 18.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!data.moderators.isNullOrEmpty()) {
                        Row (
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stringResource(id = R.string.moderator)}：",
                                fontSize = 14.sp,
                                lineHeight = 14.sp
                            )
                            LazyRow (
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                itemsIndexed(data.moderators) { index, item ->
                                    Text(
                                        text = item.toString(),
                                        fontSize = 11.sp,
                                        lineHeight = 11.sp,
                                        modifier = Modifier
                                            .clip(shape = RoundedCornerShape(3.dp))
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainer.copy(
                                                    alpha = 0.3f
                                                ),
                                            )
                                            .clickable {
                                                navHostController.navigate(
                                                    Router.UserProfileRouterEntity(
                                                        uid = null,
                                                        name = item.toString()
                                                    )
                                                )
                                            }
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = stringResource(
                            R.string.forum_detail_posts,
                            data.todayPosts.toString(),
                            data.yesterdayPosts.toString(),
                            data.posts.toString()
                        ),
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        maxLines = 1,
                        modifier = Modifier
                            .alpha(alpha = 0.7f)
                            .horizontalScroll(rememberScrollState())
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        Box (
            modifier = Modifier
                .matchParentSize()
                .alpha(progress.floatValue)
                .background(
                    brush = Brush.horizontalGradient(
                        mutableListOf(
                            Color(domainColor.intValue),
                            Color(domainColor.intValue).copy(alpha = 0.6f)
                        )
                    )
                )
        )
    }

}

@Composable
private fun BodyContent(
    showFastUpBtn: MutableState<Boolean>,
    data: ForumDetailEntity?,
    viewModel: ForumViewModel,
    fid: Int,
    onTabSelected: (Int, ScrollableState) -> Unit = { _, _ -> }
) {
    if (data == null) {
        return
    }

    viewModel.initForumChildren(data)
    val children by viewModel.forumChildren.collectAsStateWithLifecycle()

    val initPage = rememberSaveable {
        val index = children.indexOf(children.find { it.fid == fid })
        mutableIntStateOf(
            if (index < 0) {
                0
            } else {
                index
            }
        )
    }

    val states = rememberSaveable(saver = lazyListSaver) {
        mutableListOf<LazyListState>().apply {
            while (this.size < children.size) {
                this.add(LazyListState())
            }
        }
    }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { states.size },
        initialPage = initPage.intValue
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onTabSelected(page, states[page])
        }
    }
    LaunchedEffect(states[pagerState.currentPage]) {
        snapshotFlow { states[pagerState.currentPage].firstVisibleItemIndex }.collect { index ->
            showFastUpBtn.value = index > 10
        }
    }

    Column {
        ScrollableTabLayout(
            tabs = children.map { it.name ?: "NULL" },
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 5.dp),
            tabHorizontalAlignment = Alignment.Start,
            selectTabStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            ),
            unSelectTabStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            ),
            tabClick = {
                scope.launch {
                    pagerState.animateScrollToPage(it)
                }
            },
            pagerState = pagerState
        )

        HorizontalPager(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            state = pagerState,
            key = { index -> index }
        ) { page ->
            ForumThreadsScreen(
                viewModel = viewModel,
                state = states[page],
                fid = children.getOrNull(page)?.fid.toString()
            )
        }
    }

}