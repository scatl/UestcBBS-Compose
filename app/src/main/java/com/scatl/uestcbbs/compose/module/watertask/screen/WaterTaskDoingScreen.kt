package com.scatl.uestcbbs.compose.module.watertask.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.watertask.WaterTaskViewModel
import com.scatl.uestcbbs.compose.module.watertask.entity.TaskEntity
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/9/24 17:17:03
 */
@Composable
fun WaterTaskDoingScreen(

) {
    val viewModel: WaterTaskViewModel = hiltViewModel()
    val waterTaskDoingData by viewModel.waterTaskDoingData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(250)
            viewModel.getDoingTask()
        }
    }

    SwipeRefresh(
        uiState = waterTaskDoingData,
        dataEmptyContentOffset = ((-150).dp),
        onRefresh = {
            viewModel.getDoingTask(true)
        },
        onRetry = {
            viewModel.getDoingTask()
        },
        key = { index, item ->
            item.id
        }
    ) { index, item ->
        Item(
            data = item,
            viewModel = viewModel,
            modifier = Modifier
                .animateItem()
        )
    }
}

@Composable
private fun Item(
    data: TaskEntity,
    viewModel: WaterTaskViewModel,
    modifier: Modifier
) {
    val context = LocalContext.current
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val taskAwardData by viewModel.taskAwardData.collectAsStateWithLifecycle()
    val progress = rememberSaveable { mutableFloatStateOf(0f) }

    LaunchedEffect(taskAwardData) {
        if (taskAwardData.data != null && taskAwardData.data!!.taskId == data.id) {
            if (taskAwardData.isSuccess) {
                ContextCompat.getString(context, R.string.water_task_get_award_success).showToast(context)
                viewModel.getDoingTask(true)
            } else {
                ContextCompat.getString(context, R.string.water_task_get_award_fail).showToast(context)
            }
        }
    }

    val progressAnim = animateFloatAsState(
        targetValue = progress.floatValue,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    LaunchedEffect(context) {
        progress.floatValue = data.progress.toFloat() / 100
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .commonCardBg { }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = data.icon,
                modifier = Modifier
                    .size(30.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(50)
                    )
                    .clip(shape = RoundedCornerShape(50)),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = data.name.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "·" + stringResource(R.string.water_task_join_count, data.popularNum.toString()),
                fontSize = 14.sp,
                modifier = Modifier.alpha(alpha = 0.5f)
            )
        }

        Text(
            text = data.dsp.toString(),
            fontSize = 16.sp,
            modifier = Modifier.alpha(alpha = 0.7f)
        )

        Text(
            text = stringResource(R.string.water_task_award_dsp, data.award.toString().removeAllBlank().toString()),
            fontSize = 14.sp,
            modifier = Modifier.alpha(alpha = 0.8f)
        )

        Text(
            text = stringResource(R.string.water_task_progress, data.progress.toString()),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        LinearProgressIndicator(
            progress = {
                progressAnim.value
            },
            gapSize = 0.dp,
            drawStopIndicator = { },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (data.progress >= 100) {
            if (data.autoGetAward) {
                Text(
                    text = stringResource(R.string.water_task_get_award_auto),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End),
                )
            } else {
                Button(
                    modifier = Modifier
                        .align(Alignment.End),
                    onClick = {
                        viewModel.getTaskAward(data.id)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.water_task_apply_award)
                    )
                }
            }
        } else {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.water_task_time_left, data.leftTime.toString()),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                OutlinedButton(
                    onClick = {
                        showDeleteDialog.value = true
                    }
                ) {
                    Text(
                        text = stringResource(R.string.water_task_delete)
                    )
                }
            }
        }

    }

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(R.string.water_task_delete),
        text = stringResource(R.string.water_task_delete_dsp),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            viewModel.deleteDoingTask(data.id)
            showDeleteDialog.value = false
        }
    )
}