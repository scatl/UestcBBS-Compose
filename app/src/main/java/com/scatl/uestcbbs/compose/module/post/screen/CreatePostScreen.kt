package com.scatl.uestcbbs.compose.module.post.screen

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.getStatusBarHeight
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.manager.KeyboardManager
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.module.post.CreatePostBottomBar
import com.scatl.uestcbbs.compose.widget.LoadingDialog
import com.scatl.uestcbbs.compose.widget.ReplyCreditDialog
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

/**
 * Created by sca_tl at 2024/10/24 13:59:28
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    show: MutableState<Boolean>,
    bgColor: Color = MaterialTheme.colorScheme.surface,
    data: CreatePostEntity
) {
    val scope = rememberCoroutineScope()
    val viewModel: PostViewModel = hiltViewModel()
    val createReplyData by viewModel.createReplyData.collectAsStateWithLifecycle()
    val keyboardVisibility by KeyboardManager.keyboardVisibility.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val richTextState = rememberRichTextState()
    val showBottomPanel = rememberSaveable { mutableStateOf(false) }
    val showEmotionPanel = rememberSaveable { mutableStateOf(false) }
    val fullScreenMode = rememberSaveable { mutableStateOf(false) }
    val anonymousChecked = rememberSaveable { mutableStateOf(false) }

    val showRequestDialog = rememberSaveable { mutableStateOf(false) }
    val showReplyCreditDialog = rememberSaveable { mutableStateOf(false) }

    fun onKeyboardShow() {
        showBottomPanel.value = true
        showEmotionPanel.value = false
    }

    fun hide() {
        keyboardController?.hide()
        show.value = false
        showBottomPanel.value = false
        showEmotionPanel.value = false
        fullScreenMode.value = false
        anonymousChecked.value = false
    }

    LaunchedEffect(createReplyData) {
        if (createReplyData.data != null) {
            if (createReplyData.isSuccess) {
                scope.launchSafety {
                    hide()
                    delay(500)
                    if (createReplyData.data?.extCreditsUpdate != null) {
                        showReplyCreditDialog.value = true
                    }
                }
            } else {
                "发表失败，${createReplyData.errorData?.message}".showToast(context)
            }
            showRequestDialog.value = false
        }
    }

    LaunchedEffect(keyboardVisibility) {
        if (keyboardVisibility.not()) {
            if (showEmotionPanel.value.not()) {
                showBottomPanel.value = false
            }
        } else {
            onKeyboardShow()
        }
    }

    LaunchedEffect(show.value) {
        if (show.value) {
            scope.launchSafety {
                delay(100)
                showBottomPanel.value = true
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
    }

    BackHandler (
        enabled = show.value
    ) {
        show.value = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = BottomSheetDefaults.ScrimColor.copy(
                    alpha = animateFloatAsState(
                        targetValue = if (show.value) 0.32f else 0f,
                        animationSpec = tween(durationMillis = 500),
                        label = "bg_alpha"
                    ).value
                )
            )
            .clickable(
                unbound = true,
                enable = show.value,
                radius = 0.dp
            ) {
                //暂时屏蔽，点击键盘enter莫名其妙会调用
                hide()
            }
    ) {
        AnimatedVisibility(
            visible = show.value,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(radius = 0.dp) { }
                    .align(Alignment.BottomCenter)
                    .background(
                        color = bgColor,
                        shape = RoundedCornerShape(
                            topStart = animateDpAsState(
                                targetValue = if (fullScreenMode.value) 0.dp else 20.dp,
                                animationSpec = tween(durationMillis = 500),
                                label = "corner_size_ts"
                            ).value,
                            topEnd = animateDpAsState(
                                targetValue = if (fullScreenMode.value) 0.dp else 20.dp,
                                animationSpec = tween(durationMillis = 500),
                                label = "corner_size_te"
                            ).value
                        )
                    )
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .padding(
                            top = animateDpAsState(
                                targetValue = if (fullScreenMode.value) (15.dp + getStatusBarHeight(context)) else 15.dp,
                                animationSpec = tween(durationMillis = 500),
                                label = "top_padding"
                            ).value
                        )
                ) {
                    Text(
                        text = "取消",
                        modifier = Modifier
                            .clickable(unbound = true) {
                                hide()
                            }
                    )
                    Text(
                        text = "发表",
                        modifier = Modifier
                            .clickable(unbound = true) {
                                keyboardController?.hide()
                                showRequestDialog.value = true
                                viewModel.reply(
                                    requestEntity = CreatePostRequestEntity(
                                        message = richTextState.toMarkdown(),
                                        threadId = data.threadId,
                                        postId = data.postId,
                                        anonymous = data.allowAnonymous && anonymousChecked.value
                                    )
                                )
                            }
                    )
                }

                OutlinedRichTextEditor(
                    state = richTextState,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .fillMaxHeight(
                            fraction = animateFloatAsState(
                                targetValue = if (fullScreenMode.value.not()) 0.3f else 1f,
                                animationSpec = tween(durationMillis = 500),
                                label = "fraction"
                            ).value
                        )
                        .weight(1f, fill = false),
                    textStyle = TextStyle(
                        fontSize = 17.sp
                    ),
                    colors = RichTextEditorDefaults.outlinedRichTextEditorColors(
                        focusedSupportingTextColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            text = "回复：${data.replyName}",
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        onKeyboardShow()
                                    }
                                }
                            }
                        }
                )

                CreatePostBottomBar(
                    state = richTextState,
                    showBottomPanel = showBottomPanel,
                    showEmotionPanel = showEmotionPanel,
                    showFullScreen = true,
                    isFullScreen = fullScreenMode,
                    showAnonymous = data.allowAnonymous,
                    isAnonymous = anonymousChecked.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    onFullScreenClick = {
                        fullScreenMode.value = fullScreenMode.value.not()
                    }
                )
            }
        }
    }

    LoadingDialog(
        showDialog = showRequestDialog.value,
        cancelable = false,
        text = "正在发送评论，请稍候..."
    ) {
        showRequestDialog.value = false
    }

    ReplyCreditDialog(
        showDialog = showReplyCreditDialog,
        data = createReplyData.data?.extCreditsUpdate
    )
}

@Parcelize
data class CreatePostEntity(
    var threadId: Int = -1,
    var postId: Int = -1,
    var replyName: String = "",
    var allowAnonymous: Boolean = false
): Parcelable
