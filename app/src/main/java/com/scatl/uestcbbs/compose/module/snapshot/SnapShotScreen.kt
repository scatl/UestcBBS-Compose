package com.scatl.uestcbbs.compose.module.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.ThreadSnapshotManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.TIP_ID_THREAD_SNAPSHOT
import com.scatl.uestcbbs.compose.widget.Tip
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by sca_tl at 2024/9/14 15:30:20
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnapShotScreen() {
    val viewModel: SnapshotViewModel = hiltViewModel()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val snapshotData by viewModel.snapshotData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    LoadInitialDataIfNeeded(context) {
        viewModel.getAllSnapshot()
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
                        text = stringResource(R.string.snapshot_title)
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
                    if (DataStore.tipShowedId.contains(TIP_ID_THREAD_SNAPSHOT)) {
                        val tooltipState = rememberTooltipState(isPersistent = true)
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above, 4.dp),
                            tooltip = {
                                PlainTooltip (
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(end = 20.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.snapshot_detail_dsp),
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier
                                            .padding(5.dp)
                                    )
                                }
                            },
                            state = tooltipState
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable(unbound = true) {
                                        scope.launchSafety {
                                            tooltipState.show()
                                        }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                    }
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
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(paddingValues)
        ) {
            Tip(
                tip = stringResource(R.string.snapshot_detail_dsp),
                tipId = TIP_ID_THREAD_SNAPSHOT
            )

            SwipeRefresh(
                uiState = snapshotData,
                enableRefresh = false,
                enableLoadMore = false,
                listState = listState
            ) { _, item ->
                SnapshotItem(
                    item = item
                )
            }
        }

    }

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(R.string.snapshot_detail_delete_all_title),
        text = stringResource(R.string.snapshot_detail_delete_all_dsp),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            scope.launchSafety {
                val success = withContext(Dispatchers.IO) {
                    ThreadSnapshotManager.deleteAll()
                }
                ContextCompat
                    .getString(context, if (success) R.string.delete_success else R.string.delete_fail)
                    .showToast(context)
                viewModel.getAllSnapshot()
            }
            showDeleteDialog.value = false
        }
    )
}