package com.scatl.uestcbbs.compose.module.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.db.entity.FavoriteForumDBEntity
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.rememberMutableStateListOf
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.KeyboardManager
import com.scatl.uestcbbs.compose.module.forum.ForumViewModel
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.CustomTextField
import com.scatl.uestcbbs.compose.widget.EmotionPanel
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.RoundCheckBox
import com.scatl.uestcbbs.compose.widget.RoundCheckBoxDefaults
import com.scatl.uestcbbs.compose.widget.image.picker.MediaPickerConfig
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
                richTextState = richTextState
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
                selectedForum = selectedForum
            )

            BottomBar(
                state = richTextState,
                showBottomPanel = showBottomPanel,
                showEmotionPanel = showEmotionPanel,
                showVote = true,
                showReplyCredit = selectedForum.value?.detail?.replyCredit?.details?.water?.remainingCredits.toIntOrElse() > 0,
                hasVote = voteData.value != null,
                hasReplyCredit = replyCreditData.value != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                onVoteClick = {
                    showCreateVoteSheet.value = true
                },
                onReplyCreditClick = {
                    showCreateReplyCreditSheet.value = true
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: MutableState<String>,
    selectedForum: MutableState<SelectForumResult?>,
    richTextState: RichTextState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navHostController = LocalNavController.current

    TopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors().copy(
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
            Text(
                text = "发表",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(end = pagePadding)
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
                        if (richTextState
                                .toMarkdown()
                                .isEmpty()
                        ) {
                            "请输入内容".showToast(context)
                            return@clickable
                        }
                    }
                    .padding(
                        horizontal = 15.dp, vertical = 4.dp
                    )
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelectForum(
    selectedForum: MutableState<SelectForumResult?>
) {
    val viewModel: PostViewModel = hiltViewModel()
    val forumViewModel: ForumViewModel = hiltViewModel()
    val forumDetailData by forumViewModel.forumDetailData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showSelectForumSheet = rememberSaveable { mutableStateOf(false) }
    val isAddingFavorite = rememberSaveable { mutableStateOf(false) }
    val favorites = rememberMutableStateListOf(viewModel.postRepository.dataBase.getFavoriteForumDao().getAll())
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardVisibility by KeyboardManager.keyboardVisibility.collectAsState()
    val tempSelectFavorite = rememberSaveable { mutableStateOf<FavoriteForumDBEntity?>(null) }

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
                category = ForumDetailEntity.ThreadType(
                    name = tempSelectFavorite.value?.categoryName,
                    typeId = tempSelectFavorite.value?.categoryId
                )
            )
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

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clickable(unbound = true) {

                    }
            ) {
                if (selectedForum.value?.detail?.canPostAnonymously == true) {
                    RoundCheckBox(
                        isChecked = false,
                        radius = 8.dp,
                        onClick = null,
                        color = RoundCheckBoxDefaults.colors(
                            borderColor = MaterialTheme.colorScheme.primary,
                            selectedColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(0.dp)
                    )
                    Text(
                        text = "匿名发帖",
                        fontSize = 15.sp
                    )
                }
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "常用板块:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )

            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                itemsIndexed(
                    items = favorites,
                    key = { _, item -> item.forumId.toString().plus(item.categoryId) }
                ) { _, item ->
                    Text(
                        text = item.forumName.toString().plus(if (item.categoryName.isNullOrEmpty()) "" else "-${item.categoryName}"),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .animateItem()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(50)
                            )
                            .clip(shape = RoundedCornerShape(50))
                            .combinedClickable(
                                onClick = {
                                    tempSelectFavorite.value = item
                                    forumViewModel.getForumDetail(true, item.forumId.toIntOrElse())
                                },
                                onLongClick = {
                                    favorites.remove(item)
                                    viewModel.postRepository.dataBase
                                        .getFavoriteForumDao()
                                        .delete(item.id)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }

                item {
                    IconTitle(
                        icon = Icons.Outlined.Add,
                        iconTint = MaterialTheme.colorScheme.primary,
                        gap = 2.dp,
                        iconSize = 14.dp,
                        text = "添加",
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(50)
                            )
                            .clip(shape = RoundedCornerShape(50))
                            .clickable(unbound = false) {
                                scope.launchSafety {
                                    hideKeyBoard()
                                    isAddingFavorite.value = true
                                    showSelectForumSheet.value = true
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }

    SelectForumBottomSheet(
        show = showSelectForumSheet,
        onSelect = {
            forumViewModel.resetForumDetail()
            if (isAddingFavorite.value) {
                isAddingFavorite.value = false
                val dbEntity = FavoriteForumDBEntity(
                    forumId = it.detail.fid,
                    forumName = it.detail.name,
                    categoryName = it.category.name,
                    categoryId = it.category.typeId
                )
                if (favorites.contains(dbEntity)) {
                    "已经添加过该板块了".showToast(context)
                } else {
                    favorites.add(dbEntity)
                    viewModel.postRepository.dataBase.getFavoriteForumDao().insert(dbEntity)
                }
            } else {
                selectedForum.value = it
            }
        }
    )
}

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    showBottomPanel: MutableState<Boolean>,
    showEmotionPanel: MutableState<Boolean>,
    showVote: Boolean,
    hasVote: Boolean = false,
    hasReplyCredit: Boolean = false,
    showReplyCredit: Boolean,
    state: RichTextState,
    onVoteClick: (() -> Unit)? = null,
    onReplyCreditClick: (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current

    val keyboardHeight by KeyboardManager.keyboardHeight.collectAsState()
    val keyboardVisibility by KeyboardManager.keyboardVisibility.collectAsState()

    suspend fun hideKeyBoard() {
        if (keyboardVisibility) {
            keyboardController?.hide()
            delay(300)
        }
    }

    LaunchedEffect(keyboardVisibility) {
        if (keyboardVisibility.not()) {
            if (showEmotionPanel.value.not()) {
                showBottomPanel.value = false
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            RichTextStyleButton(
                onClick = {
                    showBottomPanel.value = true
                    showEmotionPanel.value = !showEmotionPanel.value
                    if (showEmotionPanel.value) {
                        keyboardController?.hide()
                    } else {
                        keyboardController?.show()
                    }
                },
                isSelected = showEmotionPanel.value,
                icon = Icons.Outlined.InsertEmoticon
            )

            RichTextStyleButton(
                onClick = {
                    navHostController.navigate(Router.WaterTaskRouterEntity)
                },
                isSelected = false,
                icon = Icons.Outlined.Attachment
            )

            RichTextStyleButton(
                onClick = {
                    scope.launchSafety {
                        hideKeyBoard()
                        val config = MediaPickerConfig.toJson(MediaPickerConfig())
                        navHostController.navigate(Router.MediaPickerRouterEntity(config))
                    }
                },
                isSelected = false,
                icon = Icons.Outlined.Photo
            )

            if (showVote) {
                RichTextStyleButton(
                    onClick = {
                        scope.launchSafety {
                            hideKeyBoard()
                            onVoteClick?.invoke()
                        }
                    },
                    isSelected = hasVote,
                    icon = Icons.Outlined.Poll
                )
            }

            AnimatedVisibility(
                visible = showReplyCredit
            ) {
                RichTextStyleButton(
                    onClick = {
                        scope.launchSafety {
                            hideKeyBoard()
                            onReplyCreditClick?.invoke()
                        }
                    },
                    isSelected = hasReplyCredit,
                    icon = Icons.Outlined.WaterDrop
                )
            }

            RichTextStyleButton(
                onClick = {
                    showBottomPanel.value = false
                    showEmotionPanel.value = false
                    keyboardController?.hide()

//                        state.addTextAfterSelection("@@@@")
//                        state.addSpanStyle(SpanStyle(color = Color.Red), TextRange(0, 4))
                },
                isSelected = false,
                icon = Icons.Outlined.AlternateEmail
            )

            Box(
                Modifier
                    .height(20.dp)
                    .width(1.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline
                    )
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H1SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H1SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h1)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H2SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H2SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h2)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H3SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H3SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h3)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H4SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H4SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h4)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H5SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H5SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h5)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H6SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H6SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h6)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = Icons.Outlined.FormatBold
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                icon = Icons.Outlined.FormatItalic
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                icon = Icons.Outlined.FormatUnderlined
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                icon = Icons.Outlined.FormatStrikethrough
            )

            RichTextStyleButton(
                onClick = {

                },
                isSelected = state.isLink,
                icon = Icons.Outlined.AddLink
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleUnorderedList()
                },
                isSelected = state.isUnorderedList,
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleOrderedList()
                },
                isSelected = state.isOrderedList,
                icon = Icons.Outlined.FormatListNumbered,
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleCodeSpan()
                },
                isSelected = state.isCodeSpan,
                icon = Icons.Outlined.Code,
            )
        }

        AnimatedVisibility(
            visible = showBottomPanel.value,
        ) {
            Box(
                modifier = Modifier
                    .height(keyboardHeight.dp)
            )

            AnimatedVisibility(
                visible = showEmotionPanel.value
            ) {
                EmotionPanel(
                    modifier = Modifier
                        .height(keyboardHeight.dp)
                        .fillMaxWidth()
                ) {
                    state.addTextAfterSelection("![${it.id}](s)")
                }
            }
        }
    }

}

@Composable
private fun RichTextStyleButton(
    onClick: () -> Unit,
    icon: Any,
    tint: Color? = null,
    isSelected: Boolean = false,
) {
    IconButton(
        modifier = Modifier
            // Workaround to prevent the rich editor
            // from losing focus when clicking on the button
            // (Happens only on Desktop)
            .focusProperties { canFocus = false },
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        ),
    ) {
        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                tint = tint ?: LocalContentColor.current,
                modifier = Modifier
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
            )
        } else if (icon is Painter){
            Icon(
                painter = icon,
                contentDescription = null,
                tint = tint ?: LocalContentColor.current,
                modifier = Modifier
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

internal val H1SpanStyle = SpanStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold)
internal val H2SpanStyle = SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
internal val H3SpanStyle = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
internal val H4SpanStyle = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
internal val H5SpanStyle = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
internal val H6SpanStyle = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)