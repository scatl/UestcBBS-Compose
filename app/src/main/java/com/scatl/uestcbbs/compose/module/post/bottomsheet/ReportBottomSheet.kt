package com.scatl.uestcbbs.compose.module.post.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.widget.CustomTextField
import com.scatl.uestcbbs.compose.widget.RoundCheckBox
import com.scatl.uestcbbs.compose.widget.RoundCheckBoxDefaults

/**
 * Created by sca_tl at 2025/2/13 15:14:39
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportBottomSheet(
    openReportBottomSheet: MutableState<Boolean>,
    pid: Int,
    fid: Int
) {
    if (openReportBottomSheet.value) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val viewModel: PostViewModel = hiltViewModel()
        val reportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val reportData = viewModel.reportData.collectAsStateWithLifecycle()
        val reportOption = remember { mutableStateOf("") }
        val reportReason = remember { mutableStateOf("") }
        val rateOptions = listOf("广告垃圾", "违规内容", "恶意灌水", "重复发帖", "其它")

        fun hide() {
            scope.launchSafety {
                reportSheetState.hide()
                openReportBottomSheet.value = false
            }
        }

        LaunchedEffect(reportData.value) {
            if (reportData.value.data != null) {
                if (reportData.value.isSuccess) {
                    hide()
                    ContextCompat.getString(context, R.string.thread_detail_report_success).showToast(context)
                } else {
                    ContextCompat.getString(context, R.string.operation_fail).showToast(context)
                }
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                hide()
            },
            sheetState = reportSheetState
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rateOptions.forEach { str ->
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(unbound = false, radius = 0.dp) {
                                    reportOption.value = str
                                }
                        ) {
                            RoundCheckBox(
                                modifier = Modifier
                                    .padding(5.dp),
                                isChecked =  reportOption.value == str,
                                borderWidth = 2.dp,
                                radius = 9.dp,
                                onClick = {
                                    reportOption.value = str
                                },
                                color = RoundCheckBoxDefaults.colors(
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(text = str)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    value = reportReason.value,
                    onValueChange = {
                        reportReason.value = it
                    },
                    placeholder = {
                        Text(
                            text = "请输入举报说明",
                            modifier = Modifier.alpha(alpha = 0.5f)
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(cardCorner),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button (
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        if (reportOption.value.isEmpty()) {
                            "请选择举报选项".showToast(context)
                        } else if (reportOption.value == rateOptions.last() && reportReason.value.isEmpty()) {
                            "请输入举报说明".showToast(context)
                        } else {
                            viewModel.report(
                                pid = pid,
                                fid = fid,
                                message = "[${reportOption.value}]${reportReason.value}"
                            )
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.report)
                    )
                }
            }
        }
    }
}