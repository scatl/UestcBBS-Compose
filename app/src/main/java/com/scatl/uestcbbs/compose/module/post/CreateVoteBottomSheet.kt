package com.scatl.uestcbbs.compose.module.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity.Poll.Option
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.rememberMutableStateListOf
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.CustomTextField
import com.scatl.uestcbbs.compose.widget.NumberInput
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Created by sca_tl at 2024/11/15 11:05:06
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun CreateVoteBottomSheet(
    showCreateVoteSheet: MutableState<Boolean>,
    data: MutableState<CreatePostRequestEntity.Poll?>
) {
    if (showCreateVoteSheet.value) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val listState = rememberLazyListState()
        val createVoteSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        val options = rememberMutableStateListOf(data.value?.options ?: mutableListOf(Option.obtain(), Option.obtain()))
        val voteDays = rememberSaveable { mutableIntStateOf(data.value?.expiration ?: 3) }
        val maxChoices = rememberSaveable { mutableIntStateOf(data.value?.maxChoices ?: 1) }
        val showVoters = rememberSaveable { mutableStateOf(data.value?.showVoters ?: false) }
        val visible = rememberSaveable { mutableStateOf(data.value?.visible ?: false) }
        val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

        fun hide() {
            scope.launchSafety {
                createVoteSheetState.hide()
                showCreateVoteSheet.value = false
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                showCreateVoteSheet.value = false
            },
            sheetGesturesEnabled = false,
            sheetState = createVoteSheetState,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "创建投票",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column (
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(cardCorner)
                        )
                        .padding(pagePadding)
                ) {
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "计票天数"
                        )
                        NumberInput(
                            initialValue = voteDays.intValue,
                            minValue = 1,
                            maxValue = 100,
                            onValueChange = {
                                voteDays.intValue = it
                            }
                        )
                    }

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "可投票项数"
                        )
                        NumberInput(
                            initialValue = maxChoices.intValue,
                            minValue = 1,
                            maxValue = options.size,
                            onValueChange = {
                                maxChoices.intValue = it
                            }
                        )
                    }

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "公开投票参与人"
                        )
                        Switch(
                            checked = showVoters.value,
                            modifier = Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                .height(35.dp),
                            onCheckedChange = {
                                showVoters.value = it
                            }
                        )
                    }

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "投票前结果可见"
                        )
                        Switch(
                            checked = visible.value,
                            modifier = Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                .height(35.dp),
                            onCheckedChange = {
                                visible.value = it
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn (
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .height(300.dp),
                ) {
                    itemsIndexed(
                        items = options,
                        key = { _ , item -> item.key }
                    ) { index, item ->
                        val text = remember { mutableStateOf(options[index].text) }
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = pagePadding)
                                .animateItem()
                        ) {
                            Box (
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .size(25.dp)
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                )
                            }

                            CustomTextField(
                                modifier = Modifier
                                    .weight(1f),
                                placeholder = {
                                    Text(
                                        text = "请输入选项描述",
                                        modifier = Modifier.alpha(alpha = 0.5f)
                                    )
                                },
                                colors = TextFieldDefaults.colors().copy(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                value = text.value,
                                onValueChange = {
                                    text.value = it
                                    options[index].text = it
                                },
                            )

                            if (options.size > 2) {
                                Icon(
                                    imageVector = Icons.Outlined.RemoveCircleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable(unbound = true) {
                                            if (maxChoices.intValue == options.size) {
                                                maxChoices.intValue = options.size - 1
                                            }
                                            options.remove(item)
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            if (options.any { it.text.isNotEmpty() }) {
                                showDeleteDialog.value = true
                                return@OutlinedButton
                            }
                            data.value = null
                            hide()
                        }
                    ) {
                        Text(
                            text = "删除"
                        )
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                options.add(
                                    Option(
                                        text = "",
                                        key = Uuid.random().toString()
                                    )
                                )
                                scope.launchSafety {
                                    listState.animateScrollToItem(options.size - 1)
                                }
                            }
                        ) {
                            Text(
                                text = "添加选项"
                            )
                        }

                        Button(
                            onClick = {
                                if (options.any { it.text.isEmpty() }) {
                                    "有空选项，请检查输入".showToast(context)
                                    return@Button
                                }
                                data.value = CreatePostRequestEntity.Poll(
                                    expiration = voteDays.intValue,
                                    maxChoices = maxChoices.intValue,
                                    showVoters = showVoters.value,
                                    visible = visible.value,
                                    options = options
                                )
                                hide()
                            }
                        ) {
                            Text(
                                text = "确认"
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        CommonAlertDialog(
            showDialog = showDeleteDialog.value,
            title = "删除投票",
            text = "确定要删除投票么？",
            onConfirmClick = {
                data.value = null
                showDeleteDialog.value = false
                hide()
            },
            onDismissRequest = {
                showDeleteDialog.value = false
            }
        )
    }
}