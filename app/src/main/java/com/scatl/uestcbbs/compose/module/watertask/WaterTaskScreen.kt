package com.scatl.uestcbbs.compose.module.watertask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.watertask.screen.WaterTaskDoingScreen
import com.scatl.uestcbbs.compose.module.watertask.screen.WaterTaskDoneScreen
import com.scatl.uestcbbs.compose.module.watertask.screen.WaterTaskFailedScreen
import com.scatl.uestcbbs.compose.module.watertask.screen.WaterTaskNewScreen
import com.scatl.uestcbbs.compose.widget.ScrollableTabLayout
import com.scatl.uestcbbs.compose.widget.TabIndicatorType
import kotlinx.coroutines.launch
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.router.LocalNavController

/**
 * Created by sca_tl at 2024/9/24 16:28:40
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTaskScreen() {
    val scope = rememberCoroutineScope()
    val viewModel: WaterTaskViewModel = hiltViewModel()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val applyTaskData by viewModel.applyTaskData.collectAsStateWithLifecycle()

    LaunchedEffect(applyTaskData) {
        if (applyTaskData.data != null) {
            if (applyTaskData.isSuccess) {
                viewModel.getNewTask(true)
                viewModel.getFailedTask(true)
                ContextCompat.getString(context, R.string.water_task_apply_success).showToast(context)
            } else {
                applyTaskData.errorData?.message.showToast(context)
            }
        }
    }

    val titles = remember { mutableStateListOf<String>() }
    if (titles.isEmpty()) {
        titles.add("进行中")
        titles.add("新任务")
        titles.add("已完成")
        titles.add("已失败")
    }

    val pagerState = rememberPagerState(
        pageCount = { titles.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        ModalBottomSheet(
            onDismissRequest = {
                navHostController.popBackStack()
            },
            sheetGesturesEnabled = false,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            sheetState = modalBottomSheetState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.8f)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
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
                            WaterTaskDoingScreen()
                        }
                        1 -> {
                            WaterTaskNewScreen()
                        }
                        2 -> {
                            WaterTaskDoneScreen()
                        }
                        3 -> {
                            WaterTaskFailedScreen()
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
        dotCounts = null,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        indicatorType = TabIndicatorType.OVAL,
        modifier = Modifier
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