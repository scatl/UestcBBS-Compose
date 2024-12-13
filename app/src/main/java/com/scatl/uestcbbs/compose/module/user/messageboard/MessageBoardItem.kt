package com.scatl.uestcbbs.compose.module.user.messageboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
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
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.MessageBoardEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestamp
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.TextInputDialog

/**
 * Created by sca_tl at 2024/7/23 7:37
 */
@Composable
fun MessageBoardItem(
    modifier: Modifier = Modifier,
    data: UserProfileEntity,
    item: MessageBoardEntity.Row,
    viewModel: UserViewModel,
    deleteCallback: ((commentId: String?) -> Unit)?
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val signedInAccount = remember {
        mutableStateOf(viewModel.userRepository.dataBase.getAccountDao().getSignedInAccount())
    }
    val showEditDialog = rememberSaveable { mutableStateOf(false) }
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val commentMsg = rememberSaveable(item) { mutableStateOf(item.message) }

    val editCommentData by viewModel.editCommentData.collectAsStateWithLifecycle()
    val deleteCommentData by viewModel.deleteCommentData.collectAsStateWithLifecycle()

    LaunchedEffect(editCommentData.data) {
        if (editCommentData.data != null && editCommentData.data?.commentId == item.commentId.toString()) {
            if (editCommentData.isSuccess) {
                commentMsg.value = editCommentData.data?.comment
                ContextCompat.getString(context, R.string.edit_success).showToast(context)
            } else {
                ContextCompat.getString(context, R.string.edit_fail).showToast(context)
            }
        }
    }

    LaunchedEffect(deleteCommentData.data) {
        if (deleteCommentData.data != null && deleteCommentData.data == item.commentId.toString()) {
            if (deleteCommentData.isSuccess) {
                deleteCallback?.invoke(deleteCommentData.data)
                ContextCompat.getString(context, R.string.delete_success).showToast(context)
            } else {
                ContextCompat.getString(context, R.string.delete_fail).showToast(context)
            }
        }
    }

    Row (
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .commonCardBg {

            }
    ) {
        AsyncImage(
            model = item.authorId.toAvatarUrl(),
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .clip(
                    shape = RoundedCornerShape(50)
                )
                .clickable {
                    navHostController.navigate(
                        Router.UserProfileRouterEntity(
                            uid = item.authorId,
                            name = item.author.toString()
                        )
                    )
                }
        )

        Column (
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = item.author.toString(),
                fontSize = 15.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = commentMsg.value.toString(),
                fontSize = 14.sp
            )
            Text(
                text = formatTimestamp(item.dateline, LocalContext.current),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.alpha(0.5f)
            )

            if (AccountManager.getSignedInAccount()?.uid == data.userSummary?.uid.toString()) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (item.authorId == signedInAccount.value?.uid.toIntOrElse()) {
                        AssistChip(
                            onClick = {
                                showEditDialog.value = true
                            },
                            label = {
                                Text(text = stringResource(id = R.string.edit))
                            },
                            shape = RoundedCornerShape(15.dp),
                            border = null,
                            colors = AssistChipDefaults.assistChipColors().copy(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                        )
                    }

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
    }

    TextInputDialog(
        initContent = commentMsg.value ?: "",
        showDialog = showEditDialog.value,
        label = stringResource(id = R.string.user_edit_comment_dsp),
        icon = null,
        title = stringResource(id = R.string.user_edit_comment),
        onDismissRequest = {
            showEditDialog.value = false
        },
        onConfirmClick = {
            if (it.isNotNullAndEmpty()) {
                viewModel.editComment(
                    commentId = item.commentId.toString(),
                    comment = it
                )
                showEditDialog.value = false
            }
        }
    )

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(id = R.string.user_delete_comment),
        text = stringResource(id = R.string.user_delete_comment_dsp),
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            viewModel.deleteComment(item.commentId.toString())
            showDeleteDialog.value = false
        }
    )
}