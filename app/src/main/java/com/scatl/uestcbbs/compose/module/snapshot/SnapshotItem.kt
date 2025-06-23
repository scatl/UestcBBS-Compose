package com.scatl.uestcbbs.compose.module.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.ThreadSnapshotManager
import com.scatl.uestcbbs.compose.module.snapshot.entity.SnapshotData
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestampYMDHMS
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created by sca_tl at 2024/9/14 16:49:57
 */
@Composable
fun SnapshotItem(
    item: SnapshotData
) {
    val viewModel: SnapshotViewModel = hiltViewModel()
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showDeleteTidDialog = rememberSaveable { mutableStateOf(false) }

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg(
                onLongClick = {
                    showDeleteTidDialog.value = true
                }
            )
    ) {
        Text(
            text = "TID: ${item.tid}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(unbound = false) {
                    navHostController.navigate(
                        Router.ThreadDetailRouterEntity(
                            id = item.tid.toIntOrElse()
                        )
                    )
                }
        )
        Text(
            text = "主题: ${item.subject}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(unbound = false) {
                    navHostController.navigate(
                        Router.ThreadDetailRouterEntity(
                            id = item.tid.toIntOrElse()
                        )
                    )
                }
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(pagePadding)
        ) {
            Text(
                text = "共${item.snapshots.size}份快照",
                modifier = Modifier
                    .padding(bottom = 5.dp)
            )
            item.snapshots.forEachIndexed { index, s ->
                Child(index, s, item)
            }
        }

    }

    CommonAlertDialog(
        showDialog = showDeleteTidDialog.value,
        title = stringResource(R.string.snapshot_detail_delete_all_title),
        text = "确认删除主题“${item.subject}”的所有快照吗？",
        onDismissRequest = {
            showDeleteTidDialog.value = false
        },
        onConfirmClick = {
            scope.launchSafety {
                val success = withContext(Dispatchers.IO) {
                    ThreadSnapshotManager.deleteAllTid(item.tid)
                }
                ContextCompat
                    .getString(context, if (success) R.string.delete_success else R.string.delete_fail)
                    .showToast(context)
                viewModel.getAllSnapshot()
            }
            showDeleteTidDialog.value = false
        }
    )
}

@Composable
private fun Child(index: Int, s: String, item: SnapshotData) {
    val navHostController = LocalNavController.current
    val viewModel: SnapshotViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    Row {
        Text(
            text = "快照${index + 1}: ${formatTimestampYMDHMS(File(s).name.toLongOrNull(), LocalContext.current)}",
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        showDeleteDialog.value = true
                    },
                    onClick = {
                        navHostController.navigate(
                            Router.ThreadDetailRouterEntity(
                                id = item.tid.toIntOrElse(),
                                snapshot = s
                            )
                        )
                    }
                )
                .alpha(alpha = 0.5f)
        )
    }

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(R.string.snapshot_detail_delete_all_title),
        text = "确认删除主题“${item.subject}”的快照${index + 1}吗？",
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            scope.launchSafety {
                val success = withContext(Dispatchers.IO) {
                    File(s).delete()
                }
                ContextCompat
                    .getString(context, if (success) R.string.delete_success else R.string.delete_fail)
                    .showToast(context)
                viewModel.getAllSnapshot()
            }
            showDeleteDialog.value = false
        }
    )
}