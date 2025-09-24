package com.scatl.uestcbbs.compose.module.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.rememberIsScrollingUp
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.history.item.BrowsingHistoryItem
import com.scatl.uestcbbs.compose.module.message.MessageViewModel
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.util.formatTimestampYMD
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.DateRangePickerDialog
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/9/10 19:40:54
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowsingHistoryScreen() {
    val viewModel: BrowsingHistoryViewModel = hiltViewModel()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val browsingHistoryData by viewModel.browsingHistoryData.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val showDatePickerDialog = rememberSaveable { mutableStateOf(false) }
    val currentStart = rememberSaveable { mutableStateOf<Long?>(null) }
    val currentEnd = rememberSaveable { mutableStateOf<Long?>(null) }

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(250)
            viewModel.getBrowsingHistory(null, null)
        }
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.browsing_history_title)
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
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable(unbound = true) {
                                showDatePickerDialog.value = true
                            }
                    )
                    Spacer(modifier = Modifier.width(pagePadding + 5.dp))
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable(unbound = true) {
                                showDeleteDialog.value = true
                            }
                    )
                    Spacer(modifier = Modifier.width(pagePadding))
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(paddingValues)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 10.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                text = if (currentStart.value == null && currentEnd.value == null) {
                    stringResource(R.string.browsing_history_all_range_tip)
                } else if (currentStart.value != null && currentEnd.value == null) {
                    stringResource(
                        R.string.browsing_history_range_tip,
                        formatTimestampYMD(currentStart.value, context),
                        formatTimestampYMD(currentStart.value, context)
                    )
                } else if (currentStart.value != null && currentEnd.value != null) {
                    if (currentStart.value == currentEnd.value) {
                        stringResource(
                            R.string.browsing_history_range_tip,
                            formatTimestampYMD(currentStart.value, context),
                            formatTimestampYMD(currentStart.value, context)
                        )
                    } else {
                        stringResource(
                            R.string.browsing_history_range_tip,
                            formatTimestampYMD(currentStart.value, context),
                            formatTimestampYMD(currentEnd.value, context)
                        )
                    }
                } else {
                    ""
                }
            )
            SwipeRefresh(
                uiState = browsingHistoryData,
                modifier = Modifier
                    .fillMaxSize(),
                enableRefresh = false,
                enableLoadMore = false,
                listState = listState
            ) { index, item ->
                BrowsingHistoryItem(
                    item = item
                )
            }
        }
    }

    DateRangePickerDialog(
        showDialog = showDatePickerDialog.value,
        title = stringResource(R.string.browsing_history_select_range_dsp),
        onDateRangeSelected = {
            currentStart.value = it.first
            currentEnd.value = it.second
            viewModel.getBrowsingHistory(it.first, it.second)
        },
        onDismiss = {
            showDatePickerDialog.value = false
        }
    )

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(R.string.browsing_history_delete_all),
        text = stringResource(R.string.browsing_history_delete_all_dsp),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            viewModel.deleteAll(currentStart.value, currentEnd.value)
            showDeleteDialog.value = false
        }
    )
}