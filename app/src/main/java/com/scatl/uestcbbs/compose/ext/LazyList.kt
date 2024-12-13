package com.scatl.uestcbbs.compose.ext

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow

/**
 * Created by sca_tl at 2024/8/19 13:54:23
 */
@Composable
fun rememberIsScrollingUp(listState: LazyListState): State<Boolean?> {
    val isScrollingUp = remember { mutableStateOf<Boolean?>(null) }
    var previousIndex by remember { mutableIntStateOf(listState.firstVisibleItemIndex) }
    var previousScrollOffset by remember { mutableIntStateOf(listState.firstVisibleItemScrollOffset) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset to listState.firstVisibleItemIndex }
            .collect { (scrollOffset, index) ->
                val isUp = if ((previousIndex == 0 && previousScrollOffset == 0) || !listState.isScrollInProgress) {
                    null
                } else if (index != previousIndex) {
                    index < previousIndex
                } else {
                    scrollOffset < previousScrollOffset
                }
                isScrollingUp.value = isUp
                previousIndex = index
                previousScrollOffset = scrollOffset
            }
    }

    return isScrollingUp
}

val lazyListSaver = listSaver<List<LazyListState>, Pair<Int, Int>>(
    save = {
        it.map {
            it.firstVisibleItemIndex to it.firstVisibleItemScrollOffset
        }
    },
    restore = {
        (it as MutableList).map { p ->
            LazyListState(p.first, p.second)
        }.toMutableList()
    }
)

fun LazyListState.calculateStickyHeadersHeight(stickyHeaderTypes: List<Any>): Int {
    val totalHeight: Int

    val stickyHeaderItems = this
        .layoutInfo
        .visibleItemsInfo
        .filter {
            stickyHeaderTypes.contains(it.contentType)
        }
    totalHeight = stickyHeaderItems.sumOf { it.size }

    return totalHeight
}