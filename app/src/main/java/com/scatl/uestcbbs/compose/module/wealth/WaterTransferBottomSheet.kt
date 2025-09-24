package com.scatl.uestcbbs.compose.module.wealth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.widget.CustomTextField
import com.scatl.uestcbbs.compose.widget.numberFilter

/**
 * Created by sca_tl at 2025/6/9 10:35:13
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTransferBottomSheet(
    show: MutableState<Boolean>,
    defaultUserName: String? = ""
) {
    if (show.value) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val viewModel: MyWealthViewModel = hiltViewModel()
        val info by viewModel.waterTransferData.collectAsStateWithLifecycle()
        val waterTransferResData by viewModel.waterTransferResData.collectAsStateWithLifecycle()
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val transferWaterCount = rememberSaveable { mutableIntStateOf(0) }
        val transferUserName = rememberSaveable { mutableStateOf(defaultUserName ?: "") }
        val transferPsw = rememberSaveable { mutableStateOf("") }
        val transferMessage = rememberSaveable { mutableStateOf("") }
        val btnEnable = rememberSaveable { mutableStateOf(true) }

        fun hide() {
            scope.launchSafety {
                sheetState.hide()
                show.value = false
            }
        }

        LoadInitialDataIfNeeded(context) {
            viewModel.getWaterTransferData()
            viewModel.resetWaterTransferResData()
        }

        LaunchedEffect(waterTransferResData) {
            if (waterTransferResData.data != null) {
                if (waterTransferResData.isSuccess) {
                    "转账成功".showToast(context)
                    hide()
                } else {
                    (waterTransferResData.errorData?.message ?: "转账失败").showToast(context)
                }
                viewModel.resetWaterTransferResData()
                btnEnable.value = true
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                show.value = false
            },
            sheetState = sheetState
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "水滴转账",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    Text(
                        text = "转账"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CustomTextField(
                        modifier = Modifier
                            .width(100.dp),
                        placeholder = {
                            Text(
                                text = "水滴数量",
                                modifier = Modifier.alpha(alpha = 0.5f)
                            )
                        },
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        visualTransformation = numberFilter,
                        shape = RoundedCornerShape(10.dp),
                        value = transferWaterCount.intValue.toString(),
                        maxLines = 1,
                        onValueChange = {
                            transferWaterCount.intValue = it.toIntOrElse()
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "水滴给"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CustomTextField(
                        modifier = Modifier,
                        placeholder = {
                            Text(
                                text = "用户名",
                                modifier = Modifier.alpha(alpha = 0.5f)
                            )
                        },
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        value = transferUserName.value,
                        maxLines = 1,
                        onValueChange = {
                            transferUserName.value = it
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "河畔登录密码："
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "转账密码",
                                modifier = Modifier.alpha(alpha = 0.5f)
                            )
                        },
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        value = transferPsw.value,
                        maxLines = 1,
                        onValueChange = {
                            transferPsw.value = it
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "留言："
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "留言",
                                modifier = Modifier.alpha(alpha = 0.5f)
                            )
                        },
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        value = transferMessage.value,
                        maxLines = 1,
                        onValueChange = {
                            transferMessage.value = it
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    text = info.data.toString().plus("。为了确保转账无误，你可以分成若干次转账，同时确保水滴数正确扣除。"),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .alpha(0.5f)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    enabled = btnEnable.value,
                    onClick = {
                        if (transferWaterCount.intValue <= 0) {
                            "水滴数量需要大于0".showToast(context)
                        } else if (transferUserName.value.isEmpty()) {
                            "请输入用户名".showToast(context)
                        } else if (transferPsw.value.isEmpty()) {
                            "请输入密码".showToast(context)
                        } else {
                            btnEnable.value = false
                            viewModel.waterTransfer(
                                waterCount = transferWaterCount.intValue,
                                userName = transferUserName.value,
                                psw = transferPsw.value,
                                message = transferMessage.value
                            )
                        }
                    }
                ) {
                    Text(
                        text = "确认转账"
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

}