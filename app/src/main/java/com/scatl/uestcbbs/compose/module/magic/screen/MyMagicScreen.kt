package com.scatl.uestcbbs.compose.module.magic.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.randomBg
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.magic.MagicViewModel
import com.scatl.uestcbbs.compose.module.magic.SCRATCH_CARD_MAGIC_ID
import com.scatl.uestcbbs.compose.module.magic.entity.MyMagicEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/9/27 11:15:34
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMagicScreen(
    showMyMagic: MutableState<Boolean>,
    openScratchCardBottomSheet: MutableState<Boolean>
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModel: MagicViewModel = hiltViewModel()
    val myMagicData by viewModel.myMagicData.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(600)
            viewModel.getMyMagic(init = true, refresh = false)
        }
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.magic_shop_my_title)
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
                                showMyMagic.value = false
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
                .padding(paddingValues),
        ) {
            SwipeRefresh(
                uiState = myMagicData,
                enableLoadMore = false,
                listState = listState,
                onRefresh = {
                    viewModel.getMyMagic(init = false, refresh = true)
                },
                onRetry = {
                    viewModel.getMyMagic(init = true, refresh = false)
                }
            ) { index, item ->
                MyMagicItem(
                    data = item,
                    openScratchCardBottomSheet = openScratchCardBottomSheet
                )
            }
        }
    }
}

@Composable
private fun MyMagicItem(
    data: MyMagicEntity,
    openScratchCardBottomSheet: MutableState<Boolean>
) {
    Column (
        modifier = Modifier
            .commonCardBg {

            }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = data.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
            )
            Text(
                text = data.name.toString(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = data.dsp.toString(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.magic_capacity_count_dsp, data.totalCount.toString(), data.totalWeight.toString()),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )

            if (data.showUseBtn) {
                Button(
                    onClick = {
                        if (data.magicId == SCRATCH_CARD_MAGIC_ID) {
                            openScratchCardBottomSheet.value = true
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.use)
                    )
                }
            }
        }
    }
}