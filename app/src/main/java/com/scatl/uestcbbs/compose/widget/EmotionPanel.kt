package com.scatl.uestcbbs.compose.widget

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.manager.EmotionManager
import com.scatl.uestcbbs.compose.manager.EmotionManager.EmotionItem

/**
 * Created by sca_tl at 2024/10/17 11:33:15
 */
@Composable
fun EmotionPanel(
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    onEmotionClick: (item: EmotionItem) -> Unit
) {
    val data = remember { mutableStateOf(EmotionManager.getEmotionPanelData()) }
    val scope = rememberCoroutineScope()
    val initialPage = rememberSaveable { mutableIntStateOf(0) }

    val pagerState = rememberPagerState(
        initialPage = initialPage.intValue,
        pageCount = {
            data.value.size
        }
    )

    Column (
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
        ) { page ->
            PagerContent(
                data = data.value[page].second,
                onEmotionClick = {
                    onEmotionClick.invoke(it)
                }
            )
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(5.dp))

        EmotionTabLayout(
            data = data.value,
            pagerState = pagerState,
            selectedColor = selectedColor,
            tabClick = {
                initialPage.intValue = it
                scope.launchSafety {
                    pagerState.animateScrollToPage(it)
                }
            }
        )
    }
}

@Composable
private fun PagerContent(
    data: List<EmotionItem>,
    onEmotionClick: (item: EmotionItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(40.dp),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        state = rememberLazyGridState(),
    ) {
        data.forEach {
            item {
                AsyncImage(
                    model = it.aPath,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp)
                        .clickable(unbound = true) {
                            onEmotionClick.invoke(it)
                        },
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun EmotionTabLayout(
    data: List<Pair<EmotionItem, List<EmotionItem>>>,
    selectedColor: Color,
    tabClick: (index: Int) -> Unit = {},
    pagerState: PagerState
) {
    val scrollState = rememberScrollState()
    val tabPositions = remember { mutableStateListOf<Pair<Int, Int>>() }
    val rowWidth = remember { mutableIntStateOf(0) }
    val indicatorWidth = remember { mutableStateListOf<Pair<Int, Int>>() }
    val indicatorHeight = remember { mutableStateListOf<Pair<Int, Int>>() }

    var selectedIndex by remember { mutableIntStateOf(pagerState.currentPage) }
    LaunchedEffect(pagerState.currentPage) {
        selectedIndex = pagerState.currentPage
    }

    Column (
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    rowWidth.intValue = coordinates.size.width
                }
                .horizontalScroll(scrollState)
        ) {
            data.forEachIndexed { index, pair ->
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
                        .clickable(enabled = true) {
                            tabClick.invoke(index)
                        }
                        .padding(
                            horizontal = 5.dp,
                            vertical = 0.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .width(
                                if (selectedIndex == index) {
                                    indicatorWidth.getOrNull(selectedIndex)?.second?.px2dp ?: 20.dp
                                } else {
                                    0.dp
                                }
                            )
                            .height(indicatorHeight.getOrNull(selectedIndex)?.second?.px2dp ?: 28.dp)
                            .background(
                                color = selectedColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .onSizeChanged {
                                if (it.width != 0) {
                                    indicatorWidth.add(Pair(index, it.width))
                                    indicatorHeight.add(Pair(index, it.height))
                                }
                            }
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = pair.first.aPath,
                            modifier = Modifier
                                .size(25.dp)
                                ,
                            contentDescription = null
                        )
                        Text(
                            text = pair.first.emotionName,
                            fontSize = 13.sp
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