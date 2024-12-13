package com.scatl.uestcbbs.compose.widget

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/7/21 10:52
 */
@Composable
fun ScrollableTabLayout(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    dotCounts: SnapshotStateList<Int>? = null,
    selectTabStyle: TextStyle,
    unSelectTabStyle: TextStyle,
    indicatorType: TabIndicatorType = TabIndicatorType.LINE,
    tabClick: (index: Int) -> Unit = {},
    tabHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    pagerState: PagerState
) {
    val scrollState = rememberScrollState()
    val tabPositions = remember { mutableStateListOf<Pair<Int, Int>>() }
    val rowWidth = remember { mutableIntStateOf(0) }
    val txtWidth = remember { mutableStateListOf<Pair<Int, Int>>() }
    val txtHeight = remember { mutableStateListOf<Pair<Int, Int>>() }

    var selectedIndex by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(pagerState.currentPage) {
        selectedIndex = pagerState.currentPage
    }

    Column (
        horizontalAlignment = tabHorizontalAlignment,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = horizontalArrangement,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    rowWidth.intValue = coordinates.size.width
                }
                .horizontalScroll(scrollState)
        ) {
            tabs.forEachIndexed { index, title ->
                Box (
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            val offset = coordinates.positionInParent().x.toInt()
                            val width = coordinates.size.width
                            if (tabPositions.size <= index) {
                                tabPositions.add(Pair(offset, width))
                            } else {
                                tabPositions[index] = Pair(offset, width)
                            }
                        }
                        .clickable {
                            tabClick(index)
                        }
                        .padding(
                            horizontal = if (indicatorType == TabIndicatorType.OVAL) 0.dp else 15.dp,
                            vertical = if (indicatorType == TabIndicatorType.OVAL) 0.dp else 10.dp
                        )
                        .padding(
                            bottom = if (indicatorType == TabIndicatorType.OVAL) 0.dp else 10.dp
                        )
                ) {
                    if (indicatorType == TabIndicatorType.OVAL) {
                        Box(
                            modifier = Modifier
                                .width(
                                    if (selectedIndex == index) {
                                        txtWidth.getOrNull(selectedIndex)?.second?.px2dp ?: 20.dp
                                    } else {
                                        0.dp
                                    }
                                )
                                .height(txtHeight.getOrNull(selectedIndex)?.second?.px2dp ?: 28.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        )
                    }

                    BadgedBox(
                        badge = {
                            Badge (
                                modifier = Modifier
                                    .offset(x = (-6).dp, y = (2).dp)
                                    .alpha(if ((dotCounts?.getOrNull(index) ?: 0) > 0) 1f else 0f)
                            ) {
                                Text(
                                    text = if ((dotCounts?.getOrNull(index) ?: 0) > 0) {
                                        dotCounts?.getOrNull(index).toString()
                                    } else {
                                        "1"
                                    },
                                    color = LocalCustomColors.current.unreadBadgeText
                                )
                            }
                        }
                    ) {
                        Text(
                            text = title,
                            style = if (selectedIndex == index) selectTabStyle else unSelectTabStyle,
                            modifier = Modifier
                                .onSizeChanged {
                                    if (it.width != 0) {
                                        txtWidth.add(Pair(index, it.width))
                                        txtHeight.add(Pair(index, it.height))
                                    }
                                }
                                .padding(
                                    horizontal = if (indicatorType == TabIndicatorType.OVAL) 15.dp else 0.dp,
                                    vertical = if (indicatorType == TabIndicatorType.OVAL) 5.dp else 0.dp
                                )
                        )
                    }

                    if (indicatorType == TabIndicatorType.LINE) {
                        HorizontalDivider(
                            color = if (selectedIndex == index) selectTabStyle.color else Color.Transparent,
                            thickness = 3.dp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                                .width(
                                    if (selectedIndex == index) {
                                        txtWidth.getOrNull(selectedIndex)?.second?.px2dp ?: 20.dp
                                    } else {
                                        0.dp
                                    }
                                )
                                .clip(
                                    shape = RoundedCornerShape(
                                        topStart = 3.dp,
                                        topEnd = 3.dp
                                    )
                                )
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedIndex) {
        if (tabPositions.isNotEmpty() && rowWidth.intValue > 0) {
            val (tabStart, tabWidth) = tabPositions.getOrElse(selectedIndex) { Pair(0, 0) }
            val tabCenter = tabStart + tabWidth / 2
            val rowCenter = rowWidth.intValue / 2
            val offset = tabCenter - rowCenter
            scrollState.animateScrollTo(value = offset, animationSpec = tween(durationMillis = 300))
        }
    }
}

enum class TabIndicatorType {
    LINE, OVAL
}

@Preview
@Composable
fun Test() {
    val tabs = listOf("Tab 1", "Tab 222", "Tab 222", "Tab 222", "Tab 222", "Tab 222", "Tab 222", "Tab 222", "Tab 222", "Tab 222")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = {tabs.size})
    val coroutineScope = rememberCoroutineScope()

    ScrollableTabLayout(
        tabs = tabs,
        //selectedIndex = pagerState.currentPage,
        selectTabStyle = TextStyle(
            color = Color.Red,
            fontSize = 17.sp
        ),
        unSelectTabStyle = TextStyle(
            color = Color.Black,
            fontSize = 17.sp
        ),
        tabClick = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(it)
            }
        },
        pagerState = pagerState
    )
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFBDBDBD))
        ) {
            Text(
                text = "Page: $page",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}
