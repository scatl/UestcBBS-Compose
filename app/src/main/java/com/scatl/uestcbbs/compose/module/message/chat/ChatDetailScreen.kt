package com.scatl.uestcbbs.compose.module.message.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.message.ChatDetailEntity
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.KeyboardManager
import com.scatl.uestcbbs.compose.module.message.MessageViewModel
import com.scatl.uestcbbs.compose.module.message.item.ChatDetailItem
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.EmotionPanel
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import com.scatl.uestcbbs.compose.widget.refresh.UiState

/**
 * Created by sca_tl at 2024/8/22 9:27:06
 */
@Composable
fun ChatDetailScreen(
    routerEntity: Router.ChatDetailRouterEntity
) {
    val viewModel: MessageViewModel = hiltViewModel()
    val chatDetailData by viewModel.chatDetailData.collectAsStateWithLifecycle()
    val onlineData by viewModel.onlineData.collectAsStateWithLifecycle()

    LoadInitialDataIfNeeded(key = routerEntity.uid) {
        viewModel.getChatDetail(
            uid = routerEntity.uid,
            init = true,
            loadMore = false
        )
        viewModel.getUserSpace(routerEntity.uid)
    }

    Scaffold (
        topBar = {
            TopBar(
                name = routerEntity.name,
                viewModel = viewModel
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
                .padding(paddingValues),
        ) {
            Box (
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .weight(1f),
            ) {
                List(
                    viewModel = viewModel,
                    chatDetailData = chatDetailData,
                    uid = routerEntity.uid
                )
            }

            BottomBar(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    name: String,
    viewModel: MessageViewModel
) {
    val navHostController = LocalNavController.current
    val onlineData by viewModel.onlineData.collectAsStateWithLifecycle()

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = pagePadding)
                )
                Text(
                    text = "",
                    modifier = Modifier
                        .background(
                            color = if (onlineData.data == true) Color.Green else Color.Gray,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .size(10.dp)
                )
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.content_dsp_chat_detail_back),
                modifier = Modifier
                    .padding(start = pagePadding)
                    .size(30.dp)
                    .unboundClickable {
                        navHostController.popBackStack()
                    }
            )
        },
        actions = {
//            Icon(
//                imageVector = Icons.Outlined.MoreVert,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(25.dp)
//                    .unboundClickable {
//
//                    }
//            )
//            Spacer(modifier = Modifier.width(pagePadding))
        }
    )
}

@Composable
private fun List(
    modifier: Modifier = Modifier,
    viewModel: MessageViewModel,
    chatDetailData: UiState<SnapshotStateList<ChatDetailEntity. Row>>,
    uid: Int
) {
    SwipeRefresh(
        uiState = chatDetailData,
        enableRefresh = false,
        reverseLayout = true,
        showNoMoreMsg = false,
        verticalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlign = Alignment.BottomCenter,
        modifier = modifier
            .padding(horizontal = pagePadding)
            .padding(bottom = pagePadding),
        onRetry = {
            viewModel.getChatDetail(
                uid = uid,
                loadMore = it == RetryType.LoadMore,
                init = it == RetryType.Init
            )
        },
        onLoadMore = {
            viewModel.getChatDetail(
                uid = uid,
                init = false,
                loadMore = true
            )
        }
    ) { index, item ->
        ChatDetailItem(
            data = item
        )
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
) {
    val inputText = rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val showBottomPanel = rememberSaveable { mutableStateOf(false) }
    val showEmotionPanel = rememberSaveable { mutableStateOf(false) }

    val keyboardHeight by KeyboardManager.keyboardHeight.collectAsState()
    val keyboardVisibility by KeyboardManager.keyboardVisibility.collectAsState()
    val navHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current).px2dp

    LaunchedEffect(keyboardVisibility) {
        if (keyboardVisibility.not()) {
            if (showEmotionPanel.value.not()) {
                showBottomPanel.value = false
            }
        }
    }

    Column (
        modifier = modifier
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(
                        topStart = pagePadding * 2,
                        topEnd = pagePadding * 2
                    )
                )
                .padding(horizontal = 15.dp, vertical = pagePadding)
        ) {
            Icon(
                imageVector = Icons.Outlined.Image,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(unbound = true) {

                    }
            )
            Icon(
                imageVector = Icons.Outlined.InsertEmoticon,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(unbound = true) {
                        showBottomPanel.value = true
                        showEmotionPanel.value = !showEmotionPanel.value
                        if (showEmotionPanel.value) {
                            keyboardController?.hide()
                        } else {
                            keyboardController?.show()
                        }
                    }
            )
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "请输入内容",
                        modifier = Modifier.alpha(alpha = 0.5f)
                    )
                },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(30.dp),
                value = inputText.value,
                maxLines = 5,
                onValueChange = {
                    inputText.value = it
                },
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    showBottomPanel.value = true
                                    showEmotionPanel.value = false
                                }
                            }
                        }
                    }
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(unbound = true) {

                    }
            )
        }

        AnimatedVisibility(
            visible = showBottomPanel.value,
        ) {
            Box(
                modifier = Modifier
                    .height(keyboardHeight.dp - navHeight)
            )

            AnimatedVisibility(
                visible = showEmotionPanel.value
            ) {
                EmotionPanel(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        .height(keyboardHeight.dp - navHeight)
                        .fillMaxWidth()
                ) {
                    //state.addTextAfterSelection("![${it.id}](s)")
                }
            }
        }
    }
}