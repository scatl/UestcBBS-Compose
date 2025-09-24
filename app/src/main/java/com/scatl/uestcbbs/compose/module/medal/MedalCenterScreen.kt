package com.scatl.uestcbbs.compose.module.medal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.medal.entity.MedalListEntity
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.CommonConfirmDialog
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/10/8 14:08:40
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedalCenterScreen() {
    val tag = "MedalCenterScreen"
    val viewModel: MedalViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val medalListData by viewModel.medalListData.collectAsStateWithLifecycle()

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(300)
            viewModel.getMedalList(init = true, refresh = false)
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
                        text = stringResource(R.string.medal_center_title)
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
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(paddingValues)
        ) {
            SwipeRefresh(
                uiState = medalListData,
                modifier = Modifier
                    .fillMaxSize(),
                enableLoadMore = false,
                listState = listState,
                onRefresh = {
                    viewModel.getMedalList(init = false, refresh = true)
                },
                onRetry = {
                    viewModel.getMedalList(init = true, refresh = false)
                }
            ) { index, item ->
                Item(
                    data = item,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun Item(
    data: MedalListEntity.MedalItem,
    viewModel: MedalViewModel
) {
    val context = LocalContext.current
    val buyMedalData by viewModel.buyMedalData.collectAsStateWithLifecycle()
    val showBuyDialog = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(buyMedalData) {
        if (buyMedalData.data != null && buyMedalData.data == data.medalId.toString()) {
            if (buyMedalData.isSuccess) {
                ContextCompat.getContextForLanguage(context)
                    .getString(R.string.medal_center_get_success, data.medalName)
                    .showToast(context)
            } else {
                (buyMedalData.errorData?.message ?: ContextCompat.getString(context, R.string.magic_use_fail_dsp)).showToast(context)
            }
            showBuyDialog.value = false
            viewModel.resetBuyMedalData()
        }
    }

    Column (
        modifier = Modifier
            .commonCardBg { }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = data.medalIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
            )
            Text(
                text = data.medalName.toString(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .weight(1f, false)
                    .padding(end = 20.dp),
                text = data.medalDsp.toString(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )

            if (data.buyDsp.isNotNullAndEmpty()) {
                OutlinedButton(
                    modifier = Modifier,
                    onClick = {
                        showBuyDialog.value = true
                    }
                ) {
                    Text(
                        text = data.buyDsp.toString()
                    )
                }
            }
        }
    }

    CommonConfirmDialog(
        showDialog = showBuyDialog,
        icon = Icons.Outlined.WorkspacePremium,
        title = stringResource(R.string.medal_center_buy_btn_dsp, data.buyDsp.toString()),
        dsp = stringResource(R.string.medal_center_buy_dialog_dsp, data.buyDsp.toString(), data.medalName.toString()),
        onDismissRequest = {
            showBuyDialog.value = false
        },
        onConfirmClick = {
            viewModel.buyMedal(data.medalId)
        }
    )
}