package com.scatl.uestcbbs.compose.module.download

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router

/**
 * Created by sca_tl at 2024/9/6 10:35:35
 */
@Composable
fun DownloadScreen(
    routerEntity: Router.DownloadRouterEntity
) {

    val context = LocalContext.current
    val isDownloadFolderUriAccessible = rememberSaveable {
        mutableStateOf(DownLoadManager.isDownloadFolderUriAccessible(context))
    }
    val downloadFolder = rememberSaveable {
        mutableStateOf(DownLoadManager.getDownloadFolder())
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            runCatching {
                val uriTree = it.data?.data
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uriTree!!, takeFlags)
                DataStore.downloadFolderUri = uriTree.toString()
                isDownloadFolderUriAccessible.value = true
                downloadFolder.value = DownLoadManager.getDownloadFolder()
            }.onFailure {
                "授权失败:${it.message}".showToast(context)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(30.dp)
    ) {
        AnimatedVisibility(visible = isDownloadFolderUriAccessible.value) {
            DownloadView(
                url = routerEntity.url,
                name = routerEntity.name,
                downloadFolder = downloadFolder,
                selectFolder = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        )
                    }
                    launcher.launch(intent)
                }
            )
        }

        AnimatedVisibility(visible = !isDownloadFolderUriAccessible.value) {
            PermissionView(
                selectFolder = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        )
                    }
                    launcher.launch(intent)
                }
            )
        }
    }
}

@Composable
private fun PermissionView(
    selectFolder: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.download_set_folder_title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.download_set_folder_dsp),
            fontSize = 16.sp,
            lineHeight = 25.sp
        )
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                selectFolder.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.next_step))
        }
    }
}

@Composable
private fun DownloadView(
    url: String?,
    name: String?,
    downloadFolder: MutableState<String>,
    selectFolder: () -> Unit
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current

    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.download_save_file_title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.download_save_file_filename, name.toString()),
            fontSize = 16.sp,
        )
        Text(
            text = stringResource(id = R.string.download_save_file_save_folder, downloadFolder.value),
            fontSize = 16.sp
        )
        if (DownLoadManager.isFileExist(context, name)) {
            Text(
                text = stringResource(id = R.string.download_save_file_overwrite_dsp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.download_modify_save_folder),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(unbound = true) {
                        selectFolder.invoke()
                    }
            )

            Button(
                onClick = {
                    DownLoadManager.getExistFile(context, name)?.delete()
                    val intent = Intent(context, DownloadService::class.java).apply {
                        putExtra("url", url)
                        putExtra("name", name)
                    }
                    context.startService(intent)
                    navHostController.popBackStack()
                }
            ) {
                Text(
                    text = if (DownLoadManager.isFileExist(context, name)) {
                        stringResource(id = R.string.download_save_file_overwrite)
                    } else {
                        stringResource(id = R.string.save)
                    }
                )
            }
        }
    }
}