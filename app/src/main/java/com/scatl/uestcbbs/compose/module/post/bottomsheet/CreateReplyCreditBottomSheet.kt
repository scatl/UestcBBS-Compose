package com.scatl.uestcbbs.compose.module.post.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.widget.NumberInput
import kotlin.math.ceil

/**
 * Created by sca_tl at 2024/11/22 9:38:21
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReplyCreditBottomSheet(
    showCreateReplyCreditSheet: MutableState<Boolean>,
    config: ForumDetailEntity.ReplyCredit.Details.Water?,
    data: MutableState<CreatePostRequestEntity.ReplyCredit?>
) {
    if (showCreateReplyCreditSheet.value) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val listState = rememberLazyListState()
        val createReplyCreditSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val creditAmount = rememberSaveable { mutableIntStateOf(data.value?.creditAmount ?: 1) }
        val totalCount = rememberSaveable { mutableIntStateOf(data.value?.count ?: 1) }
        val limitPerUser = rememberSaveable { mutableIntStateOf(data.value?.limitPerUser ?: 1) }
        val probability = rememberSaveable { mutableIntStateOf(data.value?.probability ?: 100) }

        fun hide() {
            scope.launchSafety {
                createReplyCreditSheetState.hide()
                showCreateReplyCreditSheet.value = false
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                showCreateReplyCreditSheet.value = false
            },
            sheetState = createReplyCreditSheetState,
            sheetGesturesEnabled = false,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "散水",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "每次回帖奖励水滴数："
                    )
                    NumberInput(
                        initialValue = creditAmount.intValue,
                        minValue = 1,
                        maxValue = config?.maxSingleCredits.toIntOrElse(500),
                        onValueChange = {
                            creditAmount.intValue = it.toIntOrElse()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "总共奖励次数："
                    )
                    NumberInput(
                        initialValue = totalCount.intValue,
                        minValue = 1,
                        maxValue = Int.MAX_VALUE,
                        onValueChange = {
                            totalCount.intValue = it.toIntOrElse()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "每人最多奖励次数："
                    )
                    NumberInput(
                        initialValue = limitPerUser.intValue,
                        minValue = 1,
                        maxValue = 10,
                        onValueChange = {
                            limitPerUser.intValue = it.toIntOrElse()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "中奖概率（%）："
                    )
                    NumberInput(
                        initialValue = probability.intValue,
                        readOnly = true,
                        step = 10,
                        minValue = 10,
                        maxValue = 100,
                        onValueChange = {
                            probability.intValue = it.toIntOrElse()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = buildAnnotatedString {
                        val highLightText = "税后支付${ceil(creditAmount.intValue * totalCount.intValue * (1 + (config?.taxRate ?: 0.0))).toInt()}水滴"
                        val text = "您当前拥有${config?.remainingCredits}水滴，回帖奖励总额${creditAmount.intValue * totalCount.intValue}水滴，税率${config?.taxRate}，${highLightText}。"
                        append(text)
                        val startIndex = text.indexOf(highLightText)
                        val endIndex = startIndex + highLightText.length

                        addStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            ),
                            start = startIndex,
                            end = endIndex
                        )
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                AnimatedVisibility(
                    visible = (creditAmount.intValue * totalCount.intValue) > config?.maxTotalCredits.toIntOrElse()
                ) {
                    Text(
                        text = "回帖奖励总额太大（不超过${config?.maxTotalCredits}）",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            data.value = null
                            hide()
                        }
                    ) {
                        Text(
                            text = "删除"
                        )
                    }

                    Button(
                        enabled = (creditAmount.intValue * totalCount.intValue) <= config?.maxTotalCredits.toIntOrElse(),
                        onClick = {
                            data.value = CreatePostRequestEntity.ReplyCredit(
                                count = totalCount.intValue,
                                creditAmount = creditAmount.intValue,
                                creditName = "水滴",
                                limitPerUser = limitPerUser.intValue,
                                probability = probability.intValue
                            )
                            hide()
                        }
                    ) {
                        Text(
                            text = "确认"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

    }
}