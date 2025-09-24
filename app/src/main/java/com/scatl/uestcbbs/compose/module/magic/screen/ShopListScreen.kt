package com.scatl.uestcbbs.compose.module.magic.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.magic.MagicViewModel
import com.scatl.uestcbbs.compose.module.magic.entity.MagicShopListEntity
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.TIP_ID_MAGIC_SHOP
import com.scatl.uestcbbs.compose.widget.Tip
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/9/27 11:14:05
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopListScreen(
    openDetailBottomSheet: MutableState<Boolean>,
    detailMagicId: MutableState<String?>,
    showMyMagic: MutableState<Boolean>
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModel: MagicViewModel = hiltViewModel()
    val magicListData by viewModel.magicListData.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navHostController = LocalNavController.current

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
                        text = stringResource(R.string.magic_shop_title)
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
                    if (DataStore.tipShowedId.contains(TIP_ID_MAGIC_SHOP)) {
                        val tooltipState = rememberTooltipState(isPersistent = true)
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip (
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(end = 20.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.magic_shop_dsp),
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
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable(unbound = true) {
                                showMyMagic.value = true
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
            Tip(
                tip = stringResource(R.string.magic_shop_dsp),
                tipId = TIP_ID_MAGIC_SHOP
            )
            SwipeRefresh(
                uiState = magicListData,
                modifier = Modifier
                    .fillMaxSize(),
                enableLoadMore = false,
                listState = listState,
                onRefresh = {
                    viewModel.getMagicList(init = false, refresh = true)
                },
                onRetry = {
                    viewModel.getMagicList(init = true, refresh = false)
                }
            ) { index, item ->
                ShopItem(
                    data = item,
                    onDetailClick = {
                        detailMagicId.value = it
                        openDetailBottomSheet.value = true
                    }
                )
            }
        }
    }
}

@Composable
private fun ShopItem(
    data: MagicShopListEntity.Item,
    onDetailClick: (id: String?) -> Unit
) {
    Column (
        modifier = Modifier
            .commonCardBg {
                onDetailClick.invoke(data.id)
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
                text = stringResource(R.string.magic_price, data.price.removeAllBlank().toString()),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedButton(
                onClick = {
                    onDetailClick.invoke(data.id)
                }
            ) {
                Text(
                    text = stringResource(R.string.buy)
                )
            }
        }
    }
}