package com.scatl.uestcbbs.compose.module.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.service.TopListService
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.dp2px
import com.scatl.uestcbbs.compose.ext.dpSaver
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.lerpColor
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.module.home.newpost.NewThreadScreen
import com.scatl.uestcbbs.compose.module.home.toplist.TopListScreen
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import kotlin.math.min

/**
 * Created by sca_tl at 2024/7/10 16:42:07
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val tag = "HomeScreen"

    val searchBarDefaultHeightDp = SearchBarDefaults.windowInsets.getTop(LocalDensity.current).px2dp +
            15.dp + SearchBarDefaults.InputFieldHeight

    val searchBarDefaultHeight = rememberSaveable(
        stateSaver = dpSaver
    ) {
        mutableStateOf(searchBarDefaultHeightDp)
    }

    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val tabRowHeight = rememberSaveable { mutableFloatStateOf(0f) }
    val alpha = rememberSaveable { mutableFloatStateOf(0f) }
    val alpha1 = rememberSaveable { mutableFloatStateOf(1f) }
    val showRefreshBtn = rememberSaveable { mutableStateOf(false) }

    val titleNewPost = stringResource(id = R.string.new_post_title)
    val titleNewReply = stringResource(id = R.string.new_reply_title)
    val titleHot = stringResource(id = R.string.today_hot_post_title)
    val titleDigest = stringResource(id = R.string.digest_post_title)
    val titleLife = stringResource(id = R.string.life_post_title)

    //todo
    val pageTitles = rememberSaveable {
        arrayListOf(titleNewPost, titleNewReply, titleHot, titleDigest, titleLife)
    }

    val pagerState = rememberPagerState(
        pageCount = { pageTitles.size }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {
                    NewThreadScreen(
                        topPadding = searchBarDefaultHeight.value + tabRowHeight.floatValue.px2dp,
                        page = page,
                        pagerState = pagerState,
                        showRefreshBtn = showRefreshBtn,
                        onAlphaChanged = {
                            alpha.floatValue = it
                        }
                    )
                }
                else -> {
                    TopListScreen(
                        topPadding = searchBarDefaultHeight.value + tabRowHeight.floatValue.px2dp,
                        page = page,
                        pagerState = pagerState,
                        showRefreshBtn = showRefreshBtn,
                        idListType = when(page) {
                            1 -> TopListService.IdListType.NEW_REPLY
                            2 -> TopListService.IdListType.HOT_LIST
                            3 -> TopListService.IdListType.DIGEST
                            else -> TopListService.IdListType.LIFE
                        }
                    )
                }
            }
        }

        HomeTab(
            pageTitles = pageTitles,
            pagerState = pagerState,
            searchBarDefaultHeight = searchBarDefaultHeight,
            tabRowHeight = tabRowHeight,
            alpha = alpha
        )

        HomeSearchBar(
            backgroundAlpha = if (pagerState.currentPage == 0) alpha else alpha1,
            searchBarHeight = searchBarDefaultHeight.value
        )

        BottomBtn(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            showRefreshBtn = showRefreshBtn
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeTab(
    pageTitles: List<String>,
    pagerState: PagerState,
    searchBarDefaultHeight: MutableState<Dp>,
    tabRowHeight: MutableFloatState,
    alpha: MutableFloatState
) {
    val scope = rememberCoroutineScope()
    val tabRowVerticalPadding = remember { mutableFloatStateOf(0f) }
    tabRowVerticalPadding.floatValue = 10.dp.dp2px

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        contentColor = Color.Transparent,
        containerColor = Color.Transparent,
        edgePadding = 10.dp,
        divider = { },
        indicator = { },
        modifier = Modifier
            .padding(top = searchBarDefaultHeight.value)
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = alpha.floatValue))
            .padding(vertical = tabRowVerticalPadding.floatValue.px2dp)
            .onSizeChanged {
                if (it.height > 0) {
                    tabRowHeight.floatValue =
                        it.height.toFloat() + tabRowVerticalPadding.floatValue * 2
                }
            }
    ) {
        pageTitles.forEachIndexed { index, title ->
            val selected = pagerState.currentPage == index
            Tab(
                selected = selected,
                onClick = {
                    scope.launchSafety {
                        pagerState.animateScrollToPage(index)
                    }
                }
            ) {
                HomeTabItem(
                    pagerState = pagerState,
                    alpha = alpha,
                    index = index,
                    title = title
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeTabItem(
    pagerState: PagerState,
    alpha: MutableFloatState,
    index: Int,
    title: String
) {
    val selected = pagerState.currentPage == index
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(
                        alpha = if (pagerState.currentPage == 0) {
                            alpha.floatValue + 0.5f
                        } else {
                            1f
                        }
                    )
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (pagerState.currentPage == 0) {
                if (selected) {
                    Color.White
                } else {
                    lerpColor(min(1f, alpha.floatValue), Color.White, MaterialTheme.colorScheme.primary)
                }
            } else {
                if (selected) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.primary
                }
            }
        )
    }
}

@Composable
private fun BottomBtn(
    modifier: Modifier = Modifier,
    showRefreshBtn: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navHostController = LocalNavController.current

    val bottomPadding = rememberSaveable(
        stateSaver = dpSaver
    ) {
        mutableStateOf(pagePadding + 56.dp)
    }

    DisposableEffect(context) {
        val observer = Observer<Any> {
            (it as BaseEvent.MainNavVisibleEvent).visible?.let { visible ->
                if (visible) {
                    bottomPadding.value = pagePadding + 56.dp
                } else {
                    bottomPadding.value = pagePadding
                }
            }
        }

        val liveData = SharedFlowBus.on(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY)
        liveData.observe(context as LifecycleOwner, observer)

        onDispose {
            liveData.removeObserver(observer)
        }
    }

    Column (
        modifier = modifier
            .padding(pagePadding * 2)
            .systemBarsPadding()
            .padding(
                bottom = animateDpAsState(
                    targetValue = bottomPadding.value,
                    label = "bottom_padding",
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                ).value
            )
    ) {
        FloatingActionButton(
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            onClick = {
                navHostController.navigate(Router.CreateThreadRouterEntity)
            }
        ) {
            Icon(imageVector = Icons.Outlined.Create, contentDescription = null)
        }

        AnimatedVisibility(visible = showRefreshBtn.value) {
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    SharedFlowBus.with(Event.HOME_REFRESH).tryEmit("")
                }
            ) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
            }
        }
    }
}