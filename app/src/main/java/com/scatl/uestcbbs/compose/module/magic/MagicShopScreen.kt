package com.scatl.uestcbbs.compose.module.magic

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.magic.screen.MyMagicScreen
import com.scatl.uestcbbs.compose.module.magic.screen.ShopListScreen
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.widget.HorizontalSwitchView
import com.scatl.uestcbbs.compose.widget.NumberInput
import com.scatl.uestcbbs.compose.widget.ScratchCard
import com.scatl.uestcbbs.compose.widget.StatusLayout
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * Created by sca_tl at 2024/9/26 9:29:16
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicShopScreen() {
    val tag = "MagicShopScreen"
    val viewModel: MagicViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val magicData by viewModel.magicData.collectAsStateWithLifecycle()

    val openDetailBottomSheet = rememberSaveable { mutableStateOf(false) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val openScratchCardBottomSheet = rememberSaveable { mutableStateOf(false) }
    val scratchCardSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showMyMagic = rememberSaveable { mutableStateOf(false) }
    val detailMagicId = rememberSaveable { mutableStateOf<String?>("") }

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(300)
            viewModel.getMagicList(init = true, refresh = false)
        }
    }

    BackHandler(enabled = showMyMagic.value) {
        if (showMyMagic.value) {
            showMyMagic.value = false
            scope.launchSafety {
                delay(600)
                viewModel.clearMyMagicData()
            }
        }
    }

    LaunchedEffect(openScratchCardBottomSheet.value) {
        if (!openDetailBottomSheet.value) {
            scope.launchSafety {
                viewModel.clearUseMagicData()
            }
        }
    }

//    val vditorHtml = """
//
//<!DOCTYPE html>
//<html lang="zh-cmn-Hans">
//<head>
//    <meta charset="utf-8"/>
//    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
//    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/vditor@3.10.8/dist/index.css" />
//    <script src="https://cdn.jsdelivr.net/npm/vditor@3.10.8/dist/index.min.js" defer></script>
//    <style>
//                            /* 自定义CSS使工具栏一行展示 */
//                            .toolbar {
//                                white-space: nowrap;
//                                overflow-x: auto;
//                            }
//
//                        </style>
//</head>
//<body>
//<div >
//    <div id="vditor" style="max-width: 520px; height: 100vh;"></div>
//</div>
//
//<script>
//  // Ensure the script runs after DOM is fully loaded and vditor is loaded
//  document.addEventListener('DOMContentLoaded', (event) => {
//    if (typeof Vditor !== 'undefined') {
//      new Vditor('vditor', {
//        toolbarConfig: {
//          pin: true,
//        },
//        height: window.innerHeight,
//        counter: {
//          enable: true,
//        },
//        toolbar: [
//'emoji',
//                              'headings',
//                              'bold',
//                              'italic',
//                              'strike',
//                              'link',
//                              '|',
//                              'list',
//                              'ordered-list',
//                              'check',
//                              '|',
//                              'quote',
//                              'line',
//                              'code',
//                              'inline-code',
//                              'insert-before',
//                              'insert-after',
//                              '|',
//                              'upload',
//                              'record',
//                              'table',
//                              '|',
//                              'undo',
//                              'redo',
//                              '|',
//                              'preview',
//                              'fullscreen',
//                              'edit-mode',
//                              'outline',
//
//                              'content-theme', // 内容主题
//                              'code-theme', // 代码主题
//
//
//        ],
//      });
//    } else {
//      console.error("Vditor is not loaded.");
//    }
//  });
//</script>
//</body>
//</html>
//
//
//
//                """.trimIndent()
//
//    AndroidView(
//        factory = { context ->
//            WebViewManager.getWebViewLayout(context, "").also { f ->
//                (f.getChildAt(0) as? WebView?)?.apply {
//                    loadDataWithBaseURL(null, vditorHtml, "text/html", "utf-8", null)
//                }
//            }
//        },
//        update = { webView ->
//            // This can be used to update the WebView if needed
//            (webView.getChildAt(0) as? WebView?)?.apply {
//                loadDataWithBaseURL(null, vditorHtml, "text/html", "utf-8", null)
//            }
//        },
//        modifier = Modifier.fillMaxSize()
//            .padding(top = 50.dp)
//    )

    HorizontalSwitchView(
        showContent2 = showMyMagic,
        content1 = {
            ShopListScreen(
                openDetailBottomSheet = openDetailBottomSheet,
                detailMagicId = detailMagicId,
                showMyMagic = showMyMagic
            )
        },
        content2 = {
            MyMagicScreen(
                showMyMagic = showMyMagic,
                openScratchCardBottomSheet = openScratchCardBottomSheet
            )
        }
    )

    DetailBottomSheet(
        id = detailMagicId,
        openDetailBottomSheet = openDetailBottomSheet,
        detailSheetState = detailSheetState
    )

    ScratchCardBottomSheet(
        openScratchCardBottomSheet = openScratchCardBottomSheet,
        scratchCardSheetState = scratchCardSheetState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailBottomSheet(
    id: MutableState<String?>,
    openDetailBottomSheet: MutableState<Boolean>,
    detailSheetState: SheetState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: MagicViewModel = hiltViewModel()
    val detailData by viewModel.magicDetailData.collectAsStateWithLifecycle()
    val selectedCount = rememberSaveable { mutableIntStateOf(1) }
    val buyMagicData by viewModel.buyMagicData.collectAsStateWithLifecycle()

    LaunchedEffect(buyMagicData) {
        if (buyMagicData.data != null && buyMagicData.data!!.id == id.value.toString()) {
            if (buyMagicData.isSuccess) {
                "购买成功".showToast(context)
            } else {
                buyMagicData.errorData?.message.showToast(context)
            }
        }
    }

    if (openDetailBottomSheet.value) {
        LoadInitialDataIfNeeded(context) {
            scope.launchSafety {
                selectedCount.intValue = 1
                viewModel.getMagicDetail(id.value)
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                openDetailBottomSheet.value = false
            },
            sheetState = detailSheetState
        ) {
            StatusLayout(
                uiState = detailData,
                loadingModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                emptyModifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                ) {
                    AsyncImage(
                        model = detailData.data?.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                    )
                    Text(
                        text = detailData.data?.name.toString(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = detailData.data?.dsp.toString(),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Scale,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stringResource(R.string.magic_capacity_dsp, detailData.data?.weight.toString()),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stringResource(R.string.magic_stock_dsp, detailData.data?.stock.toString()),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Discount,
                            contentDescription = null,
                            tint = LocalCustomColors.current.threadDetailReplyAward,
                            modifier = Modifier
                                .size(22.dp)
                        )
                        Text(
                            text = stringResource(R.string.magic_price_discount, detailData.data?.discountPrice.toString()),
                            color = LocalCustomColors.current.threadDetailReplyAward,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }

                    Text(
                        text = stringResource(R.string.magic_price_origin, detailData.data?.originalPrice.toString()),
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.LineThrough
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.magic_buy_count),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    NumberInput(
                        initialValue = 1,
                        minValue = 1,
                        maxValue = 100,
                        onValueChange = {
                            selectedCount.intValue = it
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.buyMagic(id.value.toString(), selectedCount.intValue)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.buy)
                        )
                    }

                    Text(
                        text = detailData.data?.availableWeight.toString().plus("，").plus(detailData.data?.otherInfo),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScratchCardBottomSheet(
    openScratchCardBottomSheet: MutableState<Boolean>,
    scratchCardSheetState: SheetState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: MagicViewModel = hiltViewModel()
    val beforeUseMagicData by viewModel.beforeUseMagicData.collectAsStateWithLifecycle()
    val isScratching = rememberSaveable { mutableStateOf(false) }
    val awardText = rememberSaveable { mutableStateOf("") }
    val confirmUseMagicData by viewModel.confirmUseMagicData.collectAsStateWithLifecycle()

    LaunchedEffect(confirmUseMagicData) {
        if (confirmUseMagicData.data != null) {
            if (confirmUseMagicData.isSuccess) {
                awardText.value = confirmUseMagicData.data!!
            } else {
                (confirmUseMagicData.errorData?.message
                    ?: ContextCompat.getString(context, R.string.magic_use_fail_dsp)
                ).showToast(context)
            }
        }
    }

    if (openScratchCardBottomSheet.value) {
        awardText.value = ""
        LoadInitialDataIfNeeded(context) {
            scope.launchSafety {
                viewModel.beforeUseMagic(SCRATCH_CARD_MAGIC_ID)
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                openScratchCardBottomSheet.value = false
            },
            //sheetGesturesEnabled = isScratching.value.not(),
            sheetState = scratchCardSheetState
        ) {
            StatusLayout(
                uiState = beforeUseMagicData,
                loadingModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                emptyModifier = Modifier.fillMaxWidth()
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp)
                        .pointerInput(Unit) {
                            //防止刮的时候触发下拉关闭手势
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
                                        val offset = it.positionChange()
                                        if (abs(offset.y) > 0f && isScratching.value) {
                                            it.consume()
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    AsyncImage(
                        model = beforeUseMagicData.data?.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                    )
                    Text(
                        text = beforeUseMagicData.data?.name.toString(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = beforeUseMagicData.data?.dsp.toString(),
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    )

                    Text(
                        text = beforeUseMagicData.data?.otherInfo.toString(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    Text(
                        text = stringResource(R.string.magic_scratch_dsp),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ScratchCard (
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        onScratchChange = {
                            isScratching.value = it
                        },
                        onScratchStarted = {
                            viewModel.confirmUseMagic(SCRATCH_CARD_MAGIC_ID)
                        }
                    ) {
                        Box (
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(200.dp)
                                .height(60.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.1f)
                                )
                        ) {
                            Text(
                                text = awardText.value,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = LocalCustomColors.current.threadDetailReplyAward
                            )
                        }
                    }
                }
            }
        }
    }
}

const val SCRATCH_CARD_MAGIC_ID = "3"
const val REGRET_MAGIC_ID = "20"