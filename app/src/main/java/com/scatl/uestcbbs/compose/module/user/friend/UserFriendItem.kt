package com.scatl.uestcbbs.compose.module.user.friend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.api.entity.user.UserFriendEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.TextInputDialog

/**
 * Created by sca_tl at 2024/9/3 13:58:39
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserFriendItem(
    modifier: Modifier,
    item: UserFriendEntity.Row,
    data: UserProfileEntity,
    viewModel: UserViewModel,
    deleteCallback: ((uid: String?) -> Unit)?
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val showEditDialog = rememberSaveable { mutableStateOf(false) }
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val note = rememberSaveable { mutableStateOf(item.note) }

    val editFriendData by viewModel.editFriendData.collectAsStateWithLifecycle()
    val deleteFriendData by viewModel.deleteFriendData.collectAsStateWithLifecycle()

    LaunchedEffect(editFriendData.data) {
        if (editFriendData.data != null && editFriendData.data?.uid == item.uid.toString()) {
            if (editFriendData.data?.success == true) {
                note.value = editFriendData.data?.note
                ContextCompat.getString(context, R.string.edit_success).showToast(context)
            } else if (editFriendData.data?.success == false) {
                ContextCompat.getString(context, R.string.edit_fail).showToast(context)
            }
        }
    }

    LaunchedEffect(deleteFriendData.data) {
        if (deleteFriendData.data != null && deleteFriendData.data?.uid == item.uid.toString()) {
            if (deleteFriendData.data?.success == true) {
                deleteCallback?.invoke(deleteFriendData.data?.uid)
                ContextCompat.getString(context, R.string.delete_success).showToast(context)
            } else if (deleteFriendData.data?.success == false) {
                ContextCompat.getString(context, R.string.delete_fail).showToast(context)
            }
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .commonCardBg { }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = item.uid.toAvatarUrl(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(50))
                    .clickable(unbound = true) {
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = item.uid,
                                name = item.username.toString()
                            )
                        )
                    }
            )

            Column {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.username.toString(),
                        fontSize = 15.sp,
                        lineHeight = 15.sp
                    )
                    if (note.value.isNotNullAndEmpty()) {
                        Text(
                            text = "(${note.value})",
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier
                                .alpha(alpha = 0.5f)
                        )
                    }
                }

                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.groupTitle.isNotNullAndEmpty() && !item.groupTitle.contains("Lv")) {
                        Text(
                            text = item.groupTitle.plus(" · "),
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            modifier = Modifier.alpha(alpha = 0.5f)
                        )
                    }

                    if (item.groupSubtitle.isNotNullAndEmpty()) {
                        Text(
                            text = item.groupSubtitle.plus(" · "),
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            modifier = Modifier.alpha(alpha = 0.5f)
                        )
                    }

                    Text(
                        text = "Lv.${item.levelId.toString()}",
                        fontSize = 13.sp,
                        lineHeight = 13.sp,
                        modifier = Modifier.alpha(alpha = 0.5f)
                    )
                }
            }
        }

        if (item.latestThread?.subject.isNotNullAndEmpty()) {
            Text(
                text = stringResource(
                    id = R.string.user_friend_latest_thread,
                    formatTimestamp(item.latestThread?.dateline, context),
                    item.latestThread?.subject.toString()
                ),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable(enabled = true) {
                        navHostController.navigate(
                            Router.ThreadDetailRouterEntity(
                                id = item.latestThread?.tid.toIntOrElse()
                            )
                        )
                    }
            )
        }

        if (AccountManager.getSignedInAccount()?.uid == data.userSummary?.uid.toString()) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth()
            ) {
                AssistChip(
                    onClick = {
                        showEditDialog.value = true
                    },
                    label = {
                        Text(text = stringResource(id = R.string.note))
                    },
                    shape = RoundedCornerShape(15.dp),
                    border = null,
                    colors = AssistChipDefaults.assistChipColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                )

                AssistChip(
                    onClick = {
                        showDeleteDialog.value = true
                    },
                    label = {
                        Text(text = stringResource(id = R.string.delete))
                    },
                    shape = RoundedCornerShape(15.dp),
                    border = null,
                    colors = AssistChipDefaults.assistChipColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                )
            }
        }
    }

    TextInputDialog(
        initContent = note.value ?: "",
        showDialog = showEditDialog.value,
        label = stringResource(id = R.string.user_edit_friend_note_dsp),
        icon = null,
        title = stringResource(id = R.string.user_edit_friend_note),
        onDismissRequest = {
            showEditDialog.value = false
        },
        onConfirmClick = {
            viewModel.editFriend(
                uid = item.uid.toString(),
                note = it ?: ""
            )
            showEditDialog.value = false
        }
    )

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(id = R.string.user_delete_friend),
        text = stringResource(id = R.string.user_delete_friend_dsp, item.username.toString()),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            viewModel.deleteFriend(item.uid.toString())
        }
    )
}