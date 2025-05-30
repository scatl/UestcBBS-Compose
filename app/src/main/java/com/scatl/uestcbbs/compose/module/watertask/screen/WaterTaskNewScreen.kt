package com.scatl.uestcbbs.compose.module.watertask.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.module.watertask.WaterTaskViewModel
import com.scatl.uestcbbs.compose.module.watertask.entity.TaskEntity
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/9/25 10:06:39
 */
@Composable
fun WaterTaskNewScreen() {
    val viewModel: WaterTaskViewModel = hiltViewModel()
    val waterTaskNewData by viewModel.waterTaskNewData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(250)
            viewModel.getNewTask()
        }
    }

    SwipeRefresh(
        uiState = waterTaskNewData,
        dataEmptyContentOffset = ((-150).dp),
        onRefresh = {
            viewModel.getNewTask(true)
        },
        onRetry = {
            viewModel.getNewTask()
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
            color = MaterialTheme.colorScheme.primary,
        )

        Button(
            modifier = Modifier
                .align(Alignment.End),
            onClick = {
                viewModel.applyNewTask(data.id)
            }
        ) {
            Text(
                text = stringResource(R.string.water_task_apply_task)
            )
        }
    }
}