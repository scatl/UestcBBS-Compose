package com.scatl.uestcbbs.compose.module.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.LoadingDialog
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/10/8 10:32:43
 */
@Composable
fun DeletePost(
    showDialog: MutableState<Boolean>,
    pid: String?,
    tid: String?,
    viewModel: PostViewModel,
    onSuccess: ((pid: String?) -> Unit)? = null
) {
    val tag = "DeletePost"
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showLoadingDialog = rememberSaveable { mutableStateOf(false) }
    val showAlertDialog = rememberSaveable { mutableStateOf(true) }
    val confirmDeleteData by viewModel.confirmDeleteData.collectAsStateWithLifecycle()

    LaunchedEffect(confirmDeleteData) {
        if (confirmDeleteData.data != null) {
            if (confirmDeleteData.isSuccess) {
                onSuccess?.invoke(pid)
                ContextCompat
                    .getString(context, R.string.delete_success)
                    .showToast(context)
            } else {
                (confirmDeleteData.errorData?.message
                    ?: ContextCompat.getString(context, R.string.magic_use_fail_dsp)
                ).showToast(context)
            }

            viewModel.resetDeleteData()
            showAlertDialog.value = false
            showDialog.value = false
            showLoadingDialog.value = false
        }
    }

    CommonAlertDialog(
        showDialog = showAlertDialog.value,
        title = stringResource(R.string.thread_detail_delete_post),
        text = stringResource(R.string.thread_detail_delete_post_dsp),
        onDismissRequest = {
            showAlertDialog.value = false
            showDialog.value = false
            showLoadingDialog.value = false
        },
        onConfirmClick = {
            XLog.tag(tag).d(pid.plus(":").plus(tid))
            scope.launchSafety {
                delay(300)
                viewModel.deletePost(pid, tid)
            }
            showLoadingDialog.value = true
            showAlertDialog.value = false
        }
    )

    LoadingDialog(
        showDialog = showLoadingDialog.value,
        text = stringResource(R.string.please_wait),
        cancelable = false
    ) {
        showAlertDialog.value = false
        showDialog.value = false
        showLoadingDialog.value = false
    }
}