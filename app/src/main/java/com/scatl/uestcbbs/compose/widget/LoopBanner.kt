package com.scatl.uestcbbs.compose.widget

import androidx.compose.animation.core.tween
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/7/16 9:43:31
 */
@Composable
fun LoopBanner(
    originDataSize: Int,
    vertical: Boolean,
    autoLoop: Boolean = true,
    userScrollEnabled: Boolean = true,
    pagerState: PagerState = rememberPagerState { originDataSize + 1 },
    modifier: Modifier,
    pageContent: @Composable PagerScope.(dataIndex: Int, pageIndex: Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val totalPageCount = pagerState.pageCount

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collectLatest { currentPage ->
            if (autoLoop) {
                delay(if (currentPage == totalPageCount - 1) 0 else 3000)
            }
            val targetPage = (currentPage + 1) % totalPageCount
            coroutineScope.launch {
                if (currentPage == totalPageCount - 1) {
                    pagerState.scrollToPage(0)
                } else {
                    if (autoLoop) {
                        pagerState.animateScrollToPage(targetPage, animationSpec = tween(ANIMATION_DURATION))
                    }
                }
            }
        }
    }

    if (vertical) {
        VerticalPager(
            state = pagerState,
            userScrollEnabled = userScrollEnabled,
            modifier = modifier
        ) { page ->
            val displayPage = if (page == totalPageCount - 1) 0 else page
            pageContent(displayPage, page)
        }
    } else {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = userScrollEnabled,
            modifier = modifier
        ) { page ->
            val displayPage = if (page == totalPageCount - 1) 0 else page
            pageContent(displayPage, page)
        }
    }

}

private const val ANIMATION_DURATION = 1000