package com.scatl.uestcbbs.compose.module.post.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.KeyboardManager
import com.scatl.uestcbbs.compose.module.forum.ForumViewModel
import com.scatl.uestcbbs.compose.module.post.PostViewModel
import com.scatl.uestcbbs.compose.module.post.bottomsheet.CreateReplyCreditBottomSheet
import com.scatl.uestcbbs.compose.module.post.bottomsheet.CreateVoteBottomSheet
import com.scatl.uestcbbs.compose.module.post.bottomsheet.SelectForumBottomSheet
import com.scatl.uestcbbs.compose.module.post.bottomsheet.SelectForumResult
import com.scatl.uestcbbs.compose.module.post.CreatePostBottomBar
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.CustomTextField
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/10/17 11:48:42
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateThreadScreen() {
    val scope = rememberCoroutineScope()
    val viewModel: PostViewModel = hiltViewModel()
    val title = rememberSaveable { mutableStateOf("") }
    val richTextState = rememberRichTextState()
    val showBottomPanel = rememberSaveable { mutableStateOf(false) }
    val showEmotionPanel = rememberSaveable { mutableStateOf(false) }
    val isAnonymous = rememberSaveable { mutableStateOf(false) }
    val keyboardHeight by KeyboardManager.keyboardHeight.collectAsState()
    val voteData = rememberSaveable { mutableStateOf<CreatePostRequestEntity.Poll?>(null) }
    val replyCreditData = rememberSaveable { mutableStateOf<CreatePostRequestEntity.ReplyCredit?>(null) }
    val selectedForum = rememberSaveable { mutableStateOf<SelectForumResult?>(null) }

    val showCreateVoteSheet = rememberSaveable { mutableStateOf(false) }
    val showCreateReplyCreditSheet = rememberSaveable { mutableStateOf(false) }

    key(Unit) {
        richTextState.config.codeSpanColor = MaterialTheme.colorScheme.primary
        richTextState.config.codeSpanStrokeColor = MaterialTheme.colorScheme.outline
    }

    Scaffold (
        topBar = {
            TopBar(
                title = title,
                selectedForum = selectedForum,
                richTextState = richTextState,
                isAnonymous = isAnonymous
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Column (
                    modifier = Modifier
                ) {
                    CustomTextField(
                        value = title.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(alpha = 0.9f),
                        onValueChange = {
                            title.value = it
                        },
                        textStyle = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 25.sp
                        ),
                        colors = TextFieldDefaults.colors().copy(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "请输入标题",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    )

                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    OutlinedRichTextEditor(
                        state = richTextState,
                        modifier = Modifier
                            .fillMaxSize(),
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
                                text = "请输入内容",
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
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
                }
            }

            SelectForum(
                selectedForum = selectedForum,
                isAnonymous = isAnonymous
            )

            CreatePostBottomBar(
                state = richTextState,
                showBottomPanel = showBottomPanel,
                showEmotionPanel = showEmotionPanel,
                showVote = true,
                showReplyCredit = selectedForum.value?.detail?.replyCredit?.details?.water?.remainingCredits.toIntOrElse() > 0,
                hasVote = voteData.value != null,
                hasReplyCredit = replyCreditData.value != null,
                showAnonymous = selectedForum.value?.detail?.canPostAnonymously == true,
                isAnonymous = isAnonymous.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                onVoteClick = {
                    showCreateVoteSheet.value = true
                },
                onReplyCreditClick = {
                    showCreateReplyCreditSheet.value = true
                },
                onAnonymousClick = {
                    isAnonymous.value = isAnonymous.value.not()
                }
            )
        }
    }

    CreateVoteBottomSheet(
        showCreateVoteSheet = showCreateVoteSheet,
        data = voteData
    )

    CreateReplyCreditBottomSheet(
        showCreateReplyCreditSheet = showCreateReplyCreditSheet,
        config = selectedForum.value?.detail?.replyCredit?.details?.water,
        data = replyCreditData
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun TopBar(
    title: MutableState<String>,
    selectedForum: MutableState<SelectForumResult?>,
    isAnonymous: MutableState<Boolean>,
    richTextState: RichTextState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navHostController = LocalNavController.current

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        title = {
            Text(
                text = "发表主题"
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(30.dp)
                    .unboundClickable {
                        navHostController.popBackStack()
                    }
            )
        },
        actions = {
            LookaheadScope {
                Text(
                    text = if (isAnonymous.value) "匿名发表" else "发表",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(end = pagePadding)
                        .animateBounds(
                            lookaheadScope = this@LookaheadScope
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(unbound = false) {
                            if (title.value.isEmpty()) {
                                "请输入标题".showToast(context)
                                return@clickable
                            }
                            if (richTextState.toMarkdown().isEmpty()) {
                                "请输入内容".showToast(context)
                                return@clickable
                            }
                        }
                        .padding(
                            horizontal = 15.dp, vertical = 4.dp
                        )
                )
            }
        }
    )
}

@Composable
private fun SelectForum(
    selectedForum: MutableState<SelectForumResult?>,
    isAnonymous: MutableState<Boolean>,
) {
    val viewModel: PostViewModel = hiltViewModel()
    val forumViewModel: ForumViewModel = hiltViewModel()
    val forumDetailData by forumViewModel.forumDetailData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showSelectForumSheet = rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardVisibility by KeyboardManager.keyboardVisibility.collectAsState()

    suspend fun hideKeyBoard() {
        if (keyboardVisibility) {
            keyboardController?.hide()
            delay(300)
        }
    }

    LaunchedEffect(forumDetailData.data) {
        if (forumDetailData.data != null && showSelectForumSheet.value.not()) {
            selectedForum.value = SelectForumResult(
                detail = forumDetailData.data!!,
                category = selectedForum.value?.category!!
            )
            if (forumDetailData.data!!.canPostAnonymously == false && isAnonymous.value) {
                isAnonymous.value = false
            }
            forumViewModel.resetForumDetail()
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = pagePadding)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "选择板块:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(50)
                        )
                        .clip(shape = RoundedCornerShape(50))
                        .clickable(unbound = false) {
                            scope.launchSafety {
                                hideKeyBoard()
                                showSelectForumSheet.value = true
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_topic),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (selectedForum.value != null) {
                            selectedForum.value?.detail?.name.toString()
                                .plus("-")
                                .plus(selectedForum.value?.category?.name)
                        } else {
                            "点击选择"
                        },
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    SelectForumBottomSheet(
        show = showSelectForumSheet,
        onSelect = { result, favorite ->
            forumViewModel.resetForumDetail()
            selectedForum.value = result
            if (favorite) {
                forumViewModel.getForumDetail(true, result.detail.fid.toIntOrElse())
            }
        }
    )
}