package com.scatl.uestcbbs.compose.module.post.commentrate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.widget.CommonIconNameView
import com.scatl.uestcbbs.compose.widget.ScrollableTabLayout
import com.scatl.uestcbbs.compose.widget.TabIndicatorType
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/8/18 10:47
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentRateScreen(
    routerEntity: Router.PostCommentAndRateRouterEntity
) {
    val viewModel: CommentAndRateViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    val titles = remember { mutableStateListOf<String>() }
    if (titles.isEmpty()) {
        titles.add(stringResource(id = R.string.comment))
        titles.add(stringResource(id = R.string.rate))
    }

    val pagerState = rememberPagerState(
        pageCount = { titles.size },
        initialPage = when (routerEntity.tab) {
            CommentRateType.COMMENT.name -> 0
            CommentRateType.RATE.name -> 1
            else -> 0
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ModalBottomSheet(
            onDismissRequest = {
                navHostController.popBackStack()
            },
            sheetState = modalBottomSheetState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column {
                Tab(
                    titles = titles,
                    pagerState = pagerState
                )

                HorizontalPager(
                    state = pagerState,
                    key = { index -> index }
                ) { page ->
                    when (page) {
                        0 -> {
                            CommentScreen(
                                routerEntity = routerEntity,
                                viewModel = viewModel,
                                sheetState = modalBottomSheetState
                            )
                        }
                        else -> {
                            RateScreen(
                                routerEntity = routerEntity,
                                viewModel = viewModel,
                                sheetState = modalBottomSheetState
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Tab(
    titles: List<String>,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()

    ScrollableTabLayout(
        tabs = titles,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        indicatorType = TabIndicatorType.OVAL,
        modifier = Modifier
            //.background(color = MaterialTheme.colorScheme.surface)
            .padding(bottom = pagePadding, start = pagePadding)
            .padding(end = pagePadding),
        selectTabStyle = TextStyle(
            color = Color.White,
            fontSize = 15.sp,
        ),
        unSelectTabStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp
        ),
        tabClick = {
            scope.launch {
                pagerState.animateScrollToPage(it)
            }
        },
        pagerState = pagerState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentScreen(
    routerEntity: Router.PostCommentAndRateRouterEntity,
    viewModel: CommentAndRateViewModel,
    sheetState: SheetState
) {
    val commentData by viewModel.commentData.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current

    LoadInitialDataIfNeeded(key = routerEntity.pid) {
        scope.launchSafety {
            delay(200)
            viewModel.getComment(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString(),
                init = true,
                loadMore = false
            )
        }
    }

    SwipeRefresh(
        uiState = commentData,
        onRetry = {
            viewModel.getComment(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString(),
                loadMore = it == RetryType.LoadMore,
                init = it == RetryType.Init
            )
        },
        onLoadMore = {
            viewModel.getComment(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString(),
                loadMore = true,
                init = false
            )
        },
        onRefresh = {
            viewModel.getComment(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString(),
                loadMore = false,
                init = false
            )
        }
    ) { index, item ->
        Column (
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = pagePadding, horizontal = 15.dp)
        ) {
            CommonIconNameView(
                iconUrl = item.authorId.toAvatarUrl(),
                name = item.author,
                date = item.dateline
            ) {
                scope.launchSafety {
                    sheetState.hide()
                    navHostController.navigate(
                        Router.UserProfileRouterEntity(
                            uid = item.authorId,
                            name = item.author.toString()
                        )
                    )
                }
            }

            Text(
                text = item.message.toString(),
                modifier = Modifier
                    .alpha(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RateScreen(
    routerEntity: Router.PostCommentAndRateRouterEntity,
    viewModel: CommentAndRateViewModel,
    sheetState: SheetState
) {
    val rateData by viewModel.rateData.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current

    LoadInitialDataIfNeeded(key = routerEntity.pid) {
        scope.launchSafety {
            delay(200)
            viewModel.getRate(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString()
            )
        }
    }

    SwipeRefresh(
        uiState = rateData,
        enableLoadMore = false,
        onRetry = {
            viewModel.getRate(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString()
            )
        },
        onRefresh = {
            viewModel.getRate(
                threadId = routerEntity.tid.toString(),
                pid = routerEntity.pid.toString()
            )
        }
    ) { index, item ->
        val waterSize by remember { mutableIntStateOf(item.credits?.water.toIntOrElse()) }
        val weiWangSize by remember { mutableIntStateOf(item.credits?.weiWang.toIntOrElse()) }

        Column (
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = pagePadding, horizontal = 15.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                CommonIconNameView(
                    iconUrl = item.userId.toAvatarUrl(),
                    name = item.username,
                    date = item.dateline
                ) {
                    scope.launchSafety {
                        sheetState.hide()
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = item.userId,
                                name = item.username.toString()
                            )
                        )
                    }
                }

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
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

            if (item.reason.isNotNullAndEmpty()) {
                Text(
                    text = item.reason.toString(),
                    modifier = Modifier
                        .alpha(alpha = 0.7f)
                )
            }
        }
    }
}

enum class CommentRateType {
    COMMENT, RATE
}