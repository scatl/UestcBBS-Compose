package com.scatl.uestcbbs.compose.module.post.commentrate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.widget.CustomTextField
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/10/22 11:41:38
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentBottomSheet(
    openCommentBottomSheet: MutableState<Boolean>,
    threadId: Int,
    postId: Int,
    onSuccess: (postId: Int, message: String) -> Unit
) {
    val viewModel: PostViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val commentData by viewModel.commentData.collectAsStateWithLifecycle()
    val message = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

//    LaunchedEffect(openCommentBottomSheet.value) {
//        if (openCommentBottomSheet.value) {
//            scope.launchSafety {
//                delay(400)
//                focusRequester.requestFocus()
//                keyboardController?.show()
//            }
//        }
//    }

    fun hide() {
        scope.launchSafety {
//            keyboardController?.hide()
//            focusRequester.freeFocus()
            commentSheetState.hide()
            openCommentBottomSheet.value = false
        }
    }

    LaunchedEffect(commentData) {
        if (commentData.data != null) {
            if (commentData.isSuccess) {
                onSuccess.invoke(postId, message.value)
                hide()
                "点评成功".showToast(context)
            } else {
                (commentData.errorData?.message ?: "点评失败").showToast(context)
            }
            viewModel.resetCommentData()
        }
    }

    ModalBottomSheet(
        sheetGesturesEnabled = false,
        onDismissRequest = {
            openCommentBottomSheet.value = false
        },
        sheetState = commentSheetState
    ) {
        Column (
            modifier = Modifier
                .padding(horizontal = pagePadding * 2)
        ) {
            Text(
                text = "点评",
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                value = message.value,
                onValueChange = {
                    if (it.length <= MAX_LENGTH) {
                        message.value = it
                    }
                },
                placeholder = {
                    Text(
                        text = "请输入点评内容。注意和评论区分哦，点评不支持被回复，编辑，删除等操作！",
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
                    .height(150.dp)
                    .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "还可输入${MAX_LENGTH - message.value.length}个字符",
                fontSize = 14.sp,
                modifier = Modifier.alpha(0.5f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    viewModel.comment(postId, threadId, message.value)
                }
            ) {
                Text(
                    text = "确认"
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private const val MAX_LENGTH = 255