package com.scatl.uestcbbs.compose.module.post.commentrate

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.widget.StatusLayout
import com.scatl.uestcbbs.compose.widget.TIP_ID_RATE
import com.scatl.uestcbbs.compose.widget.Tip
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Created by sca_tl at 2025/2/8 14:22:55
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateBottomSheet(
    openRateBottomSheet: MutableState<Boolean>,
    threadId: Int,
    postId: Int,
    onSuccess: (postId: Int) -> Unit
) {
    if (openRateBottomSheet.value) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val viewModel: PostViewModel = hiltViewModel()
        val rateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val rateOptionData by viewModel.rateOptionData.collectAsStateWithLifecycle()
        val rateData = viewModel.rateData.collectAsStateWithLifecycle()
        val sliderPosition = remember { mutableFloatStateOf(0f) }
        var minValue by remember { mutableFloatStateOf( 0f) }
        var maxValue by remember { mutableFloatStateOf(0f) }

        fun hide() {
            scope.launchSafety {
                rateSheetState.hide()
                openRateBottomSheet.value = false
            }
        }

        LaunchedEffect(rateData.value) {
            if (rateData.value.data != null) {
                if (rateData.value.isSuccess) {
                    onSuccess.invoke(postId)
                    hide()
                    ContextCompat.getString(context, R.string.thread_detail_rate_success).showToast(context)
                } else {
                    ContextCompat.getString(context, R.string.operation_fail).showToast(context)
                }
            }
        }

        LoadInitialDataIfNeeded(context) {
            scope.launchSafety {
                viewModel.getRateOption(postId.toString())
            }
        }

        LaunchedEffect(rateOptionData.data) {
            minValue = rateOptionData.data?.credits?.water?.min?.toFloat() ?: 0f
            maxValue = rateOptionData.data?.credits?.water?.max?.toFloat() ?: 0f
        }

        ModalBottomSheet(
            onDismissRequest = {
                hide()
            },
            sheetState = rateSheetState
        ) {
            StatusLayout(
                uiState = rateOptionData,
                loadingModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                emptyModifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rate),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Slider(
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            value = sliderPosition.floatValue,
                            onValueChange = { sliderPosition.floatValue = it },
                            colors = SliderDefaults.colors(
                                activeTickColor = Color.Transparent,
                                inactiveTickColor = Color.Transparent,
                            ),
                            thumb = {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sliderPosition.floatValue.roundToInt().toString(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            steps = kotlin.math.max((maxValue - minValue).toInt() - 1, 1),
                            valueRange = minValue .. maxValue
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Tip(
                        tip = "1、拖动滑块选择需要评分的积分\n"
                            .plus("2、今天剩余增加他人积分额度：${rateOptionData.data?.credits?.water?.limit24hPositive}，今天剩余扣除他人积分额度：${rateOptionData.data?.credits?.water?.limit24hNegative}\n")
                            .plus("3、增加他人积分会扣除自身相应积分\n")
                            .plus("4、扣除他人积分也会扣除自身相应积分，并且需要收取${rateOptionData.data?.credits?.water?.taxRateNegative}倍手续费，当前需要扣除手续费为：" +
                                    "${ceil((rateOptionData.data?.credits?.water?.taxRateNegative ?: 0.0) * abs(if (sliderPosition.floatValue.roundToInt() > 0) 0 else sliderPosition.floatValue.roundToInt()))}"),
                        tipId = TIP_ID_RATE,
                        bgColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        confirmText = "",
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                    Button (
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        onClick = {
                            if (sliderPosition.floatValue.roundToInt() == 0) {
                                "积分不能是0".showToast(context)
                            } else {
                                viewModel.rate(postId.toString(), sliderPosition.floatValue.roundToInt())
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.rate)
                        )
                    }
                }
            }
        }
    }
}