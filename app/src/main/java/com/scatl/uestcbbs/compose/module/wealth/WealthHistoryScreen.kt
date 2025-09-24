package com.scatl.uestcbbs.compose.module.wealth

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.linkNavigate
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2025/6/9 17:10:23
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthHistoryScreen() {
    val tag = "WealthHistoryScreen"
    val viewModel: MyWealthViewModel = hiltViewModel()
    val historyData by viewModel.historyData.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentIncome = rememberSaveable { mutableIntStateOf(0) }
    val currentType = rememberSaveable { mutableIntStateOf(0) }

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(300)
            viewModel.getWealthHistory(
                income = currentIncome.intValue.toString(),
                type = currentType.intValue.toString(),
                loadMore = false,
                init = true
            )
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "财富历史"
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
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
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 15.dp)
            ) {
                Text(
                    text = "收支：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row (
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    listOf(
                        ("不限" to 0),
                        ("收入" to 1),
                        ("支出" to -1),
                    ).forEach { item ->
                        FilterChip(
                            label = {
                                Text(
                                    text = item.first,
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(15.dp),
                            border = null,
                            colors = FilterChipDefaults.filterChipColors().copy(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            selected = currentIncome.intValue == item.second,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                currentIncome.intValue = item.second
                                viewModel.getWealthHistory(
                                    income = currentIncome.intValue.toString(),
                                    type = currentType.intValue.toString(),
                                    loadMore = false,
                                    init = false
                                )
                            }
                        )
                    }
                }
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 15.dp)
            ) {
                Text(
                    text = "类型：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row (
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    listOf(
                        ("不限" to 0),
                        ("水滴" to 2),
                        ("威望" to 1),
                        ("奖励券" to 6)
                    ).forEach { item ->
                        FilterChip(
                            label = {
                                Text(
                                    text = item.first,
                                    fontSize = 12.sp
                                )
                            },
                            shape = RoundedCornerShape(15.dp),
                            border = null,
                            colors = FilterChipDefaults.filterChipColors().copy(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            selected = currentType.intValue == item.second,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                currentType.intValue = item.second
                                viewModel.getWealthHistory(
                                    income = currentIncome.intValue.toString(),
                                    type = currentType.intValue.toString(),
                                    loadMore = false,
                                    init = false
                                )
                            }
                        )
                    }
                }
            }

            SwipeRefresh(
                uiState = historyData,
                onRefresh = {
                    viewModel.getWealthHistory(
                        income = currentIncome.intValue.toString(),
                        type = currentType.intValue.toString(),
                        loadMore = false,
                        init = false
                    )
                },
                onRetry = {
                    viewModel.getWealthHistory(
                        income = currentIncome.intValue.toString(),
                        type = currentType.intValue.toString(),
                        loadMore = it == RetryType.LoadMore,
                        init = it == RetryType.Init
                    )
                },
                onLoadMore = {
                    viewModel.getWealthHistory(
                        income = currentIncome.intValue.toString(),
                        type = currentType.intValue.toString(),
                        loadMore = true,
                        init = false
                    )
                }
            ) { _, item ->
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .commonCardBg(
                            bgColor = MaterialTheme.colorScheme.surface,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            padding = PaddingValues.Zero
                        ) {
                            linkNavigate(
                                url = item.link,
                                uriHandler = uriHandler,
                                navHostController = navHostController
                            )
                        }
                ) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(15.dp)
                    ) {
                        Text(
                            text = "变更：".plus(item.change),
                            color = if (item.increase) {
                                LocalCustomColors.current.threadTitleReplyAwardStart
                            } else {
                                LocalCustomColors.current.threadTitleRecommendStart
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "类型：".plus(item.action),
                            fontSize = 15.sp,
                            modifier = Modifier.alpha(0.6f)
                        )
                        Text(
                            text = "详情：".plus(item.detail),
                            fontSize = 15.sp,
                            modifier = Modifier.alpha(0.6f)
                        )
                        Text(
                            text = "时间：".plus(item.time),
                            fontSize = 15.sp,
                            modifier = Modifier.alpha(0.6f)
                        )
                    }

                    Text(
                        text = if (item.increase) "收入" else "支出",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        modifier = Modifier
                            .background(
                                color = if (item.increase) {
                                    LocalCustomColors.current.threadTitleReplyAwardStart
                                } else {
                                    LocalCustomColors.current.threadTitleRecommendStart
                                },
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = cardCorner,
                                    bottomEnd = 0.dp,
                                    bottomStart = cardCorner
                                )
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}