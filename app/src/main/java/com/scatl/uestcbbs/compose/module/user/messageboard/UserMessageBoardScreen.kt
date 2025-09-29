package com.scatl.uestcbbs.compose.module.user.messageboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.MessageBoardEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.module.user.UserProfilePage
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.StickyLayoutController
import com.scatl.uestcbbs.compose.widget.refresh.RefreshIndicator
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh

/**
 * Created by sca_tl at 2024/7/22 21:02
 */
@Composable
fun UserMessageBoardScreen(
    stickyLayoutController: State<StickyLayoutController>,
    data: UserProfileEntity,
    viewModel: UserViewModel,
    state: LazyListState = rememberLazyListState()
) {
    val messageBoardData by viewModel.messageBoardData.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (!viewModel.isPageInitialized(UserProfilePage.MESSAGE_BOARD)) {
            viewModel.getUserMessageBoard(loadMore = false, init = true)
            viewModel.setPageInitialized(UserProfilePage.MESSAGE_BOARD)
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        EditMessage(
            viewModel = viewModel,
            stickyLayoutController = stickyLayoutController,
            addCommentCallBack = {
                messageBoardData.data?.add(0, it)
            }
        )

        SwipeRefresh(
            uiState = messageBoardData,
            listState = state,
            modifier = Modifier.fillMaxSize(),
            onRefresh = {
                viewModel.getUserMessageBoard(loadMore = false, init = false)
            },
            onRetry = {
                viewModel.getUserMessageBoard(
                    loadMore = it == RetryType.LoadMore,
                    init = it == RetryType.Init
                )
            },
            onLoadMore = {
                viewModel.getUserMessageBoard(loadMore = true, init = false)
            }
        ) { index, item ->
            MessageBoardItem(
                modifier = Modifier.animateItem(),
                data = data,
                item = item,
                viewModel = viewModel,
                deleteCallback = { commentId ->
                    commentId?.let {
                        messageBoardData.data?.removeIf { it.commentId.toString() == commentId }
                    }
                }
            )
        }
    }
}

@Composable
private fun EditMessage(
    viewModel: UserViewModel,
    stickyLayoutController: State<StickyLayoutController>,
    addCommentCallBack: (data: MessageBoardEntity.Row) -> Unit
) {
    val addCommentData by viewModel.addCommentData.collectAsStateWithLifecycle()
    val isExpanded = rememberSaveable { mutableStateOf(false) }
    val commentMsg = rememberSaveable { mutableStateOf("") }
    val rotationDegree by animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "arrow_rotation"
    )

    LaunchedEffect(addCommentData.data) {
        if (addCommentData.data != null && addCommentData.isSuccess) {
            addCommentCallBack.invoke(addCommentData.data!!)
            commentMsg.value = ""
            isExpanded.value = false
        }
    }

    Spacer(modifier = Modifier.height(pagePadding))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = pagePadding)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(pagePadding)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(unbound = false) {
                    isExpanded.value = !isExpanded.value
                }
                .padding(horizontal = pagePadding),
        ) {
            IconTitle(
                icon = Icons.Outlined.EditNote,
                iconSize = 20.dp,
                text = "发表留言",
                textStyle = TextStyle()
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .rotate(rotationDegree)
            )
        }

        AnimatedVisibility(visible = isExpanded.value) {
            Column (
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = commentMsg.value,
                    onValueChange = {
                        commentMsg.value = it
                    },
                    label = { Text(text = stringResource(R.string.user_name)) },
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = pagePadding),
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        stickyLayoutController.value.scrollToBody?.invoke(300)
                                    }
                                }
                            }
                        }
                )

                Button(
                    enabled = commentMsg.value.isNotNullAndEmpty(),
                    modifier = Modifier.padding(end = pagePadding),
                    onClick = {
                        viewModel.addComment(commentMsg.value)
                    }
                ) {
                    Text(text = stringResource(id = R.string.user_add_comment))
                }
            }
        }
    }
}