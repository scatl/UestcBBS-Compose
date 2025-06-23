package com.scatl.uestcbbs.compose.module.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.entity.search.SearchSummaryEntity
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.post.item.CommonThreadItem
import com.scatl.uestcbbs.compose.module.search.entity.SearchSummaryData
import com.scatl.uestcbbs.compose.module.search.entity.SearchSummaryData.TitleData
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.CommonIconNameView
import com.scatl.uestcbbs.compose.widget.CustomTextField
import com.scatl.uestcbbs.compose.widget.HorizontalSwitchView
import com.scatl.uestcbbs.compose.widget.IconPosition
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/9/11 16:16:08
 */
@Composable
fun SearchScreen() {
    val viewModel: SearchViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val inputText = rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val showFullResult = rememberSaveable { mutableStateOf(false) }
    val currentFullSearchType = rememberSaveable { mutableStateOf(SearchSummaryData.SearchSummaryDataType.THREAD.name) }

    LoadInitialDataIfNeeded(context) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(showFullResult.value) {
        if (!showFullResult.value) {
            scope.launchSafety {
                delay(600)
                viewModel.clearFullSearchResult()
            }
        }
    }

    BackHandler (enabled = showFullResult.value) {
        showFullResult.value = false
    }

    Box (
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalSwitchView(
            showContent2 = showFullResult,
            content1 = {
                SearchContent(
                    focusRequester = focusRequester,
                    inputText = inputText,
                    showFullResult = showFullResult,
                    viewModel = viewModel,
                    currentFullSearchType = currentFullSearchType
                )
            },
            content2 = {
                FullSearchResult(
                    focusRequester = focusRequester,
                    inputText = inputText,
                    showFullResult = showFullResult,
                    currentFullSearchType = currentFullSearchType,
                    viewModel = viewModel
                )
            }
        )
    }
}

@Composable
private fun SearchContent(
    focusRequester: FocusRequester,
    inputText: MutableState<String>,
    showFullResult: MutableState<Boolean>,
    viewModel: SearchViewModel,
    currentFullSearchType: MutableState<String>
) {
    val navHostController = LocalNavController.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .systemBarsPadding()
            .padding(top = pagePadding * 2)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(bottom = 10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .alpha(alpha = 0.8f)
                    .clickable(unbound = true) {
                        focusRequester.freeFocus()
                        navHostController.popBackStack()
                    }
            )

            CustomTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                placeholder = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.alpha(alpha = 0.5f)
                        )
                        Text(
                            text = stringResource(R.string.search_text_filed_hint),
                            modifier = Modifier.alpha(alpha = 0.5f)
                        )
                    }
                },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(30.dp),
                value = inputText.value,
                maxLines = 1,
                onValueChange = {
                    if (inputText.value != it) {
                        inputText.value = it
                        viewModel.getSearchSummary(it)
                    }
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        SearchSummary(
            keyword = inputText,
            focusRequester = focusRequester,
            showFullResult = showFullResult,
            currentFullSearchType = currentFullSearchType,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
private fun SearchSummary(
    keyword: MutableState<String>,
    focusRequester: FocusRequester,
    showFullResult: MutableState<Boolean>,
    currentFullSearchType: MutableState<String>,
    modifier: Modifier
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val searchSummaryData by viewModel.searchSummaryData.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect {
            keyboardController?.hide()
        }
    }

    SwipeRefresh(
        uiState = searchSummaryData,
        modifier = modifier
            .fillMaxSize(),
        listState = listState,
        enableRefresh = false,
        enableLoadMore = false
    ) { index, item ->
        when (item.itemType) {
            SearchSummaryData.SearchSummaryDataType.TITLE -> {
                Title(
                    data = (item as SearchSummaryData.Title).data,
                    currentFullSearchType = currentFullSearchType,
                    showFullResult = showFullResult
                )
            }
            SearchSummaryData.SearchSummaryDataType.THREAD -> {
                Thread(
                    data = (item as SearchSummaryData.Thread).data
                )
            }
            SearchSummaryData.SearchSummaryDataType.User -> {
                User(
                    data = (item as SearchSummaryData.User).data
                )
            }
        }
    }
}

@Composable
private fun Title(
    data: TitleData,
    currentFullSearchType: MutableState<String>,
    showFullResult: MutableState<Boolean>
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(pagePadding)
    ) {
        Text(
            text = if (data.type == SearchSummaryData.SearchSummaryDataType.THREAD) {
                stringResource(R.string.search_result_thread_title, data.count)
            } else {
                stringResource(R.string.search_result_user_title, data.count)
            }
        )

        if (data.count > 5) {
            IconTitle(
                icon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                iconSize = 24.dp,
                iconTint = MaterialTheme.colorScheme.primary,
                iconPosition = IconPosition.RIGHT,
                text = stringResource(R.string.view_more),
                gap = 0.dp,
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .clickable(unbound = true) {
                        currentFullSearchType.value = data.type.name
                        showFullResult.value = true
                    }
            )
        }
    }
}

@Composable
private fun Thread(
    data: SearchSummaryEntity.Thread
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg {
                keyboardController?.hide()
                navHostController.navigate(
                    Router.ThreadDetailRouterEntity(
                        id = data.threadId.toIntOrElse()
                    )
                )
            }
    ) {
        CommonIconNameView(
            iconUrl = data.authorId.toAvatarUrl(),
            name = data.author.toString(),
            date = data.dateline,
            onClick = {
                keyboardController?.hide()
                navHostController.navigate(
                    Router.UserProfileRouterEntity(
                        uid = data.authorId,
                        name = data.author.toString()
                    )
                )
            }
        )

        Text(
            text = data.subject.toString(),
            modifier = Modifier
                .padding(top = 5.dp)
        )

        if (data.tidMatch) {
            Text(
                text = stringResource(R.string.search_result_thread_id_match_dsp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun User(
    data: SearchSummaryEntity.User,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg {
                keyboardController?.hide()
                navHostController.navigate(
                    Router.UserProfileRouterEntity(
                        uid = data.uid,
                        name = data.username.toString()
                    )
                )
            }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            AsyncImage(
                model = data.uid.toAvatarUrl(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(50))
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = data.username.toString(),
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                ) {
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 5.dp)
                    ) {
                        Text(
                            text = if (data.groupTitle?.contains("Lv") == false) {
                                data.groupTitle.toString()
                            } else {
                                "Lv${data.levelId}"
                            },
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            lineHeight = 12.sp
                        )
                    }

                    if (data.groupSubtitle.isNotNullAndEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 2.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 5.dp)
                        ) {
                            Text(
                                text = data.groupSubtitle.toString(),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }

        if (data.uidMatch) {
            Text(
                text = stringResource(R.string.search_result_user_id_match_dsp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullSearchResult(
    focusRequester: FocusRequester,
    inputText: MutableState<String>,
    showFullResult: MutableState<Boolean>,
    currentFullSearchType: MutableState<String>,
    viewModel: SearchViewModel
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val searchThreadData by viewModel.searchThreadData.collectAsStateWithLifecycle()
    val searchUserData by viewModel.searchUserData.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LoadInitialDataIfNeeded(inputText.value) {
        scope.launchSafety {
            delay(600)
            if (currentFullSearchType.value == SearchSummaryData.SearchSummaryDataType.THREAD.name) {
                viewModel.searchThread(
                    keyword = inputText.value,
                    init = true,
                    loadMore = false
                )
            } else {
                viewModel.searchUser(
                    keyword = inputText.value,
                    init = true,
                    loadMore = false
                )
            }
        }
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.search_result_full_title, inputText.value)
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
                                showFullResult.value = false
                            }
                    )
                }
            )
        }
    ) { paddingValues ->
        SwipeRefresh(
            uiState = if (currentFullSearchType.value == SearchSummaryData.SearchSummaryDataType.THREAD.name) {
                searchThreadData
            } else {
                searchUserData
            },
            enableRefresh = false,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(paddingValues),
            onLoadMore = {
                if (currentFullSearchType.value == SearchSummaryData.SearchSummaryDataType.THREAD.name) {
                    viewModel.searchThread(
                        keyword = inputText.value,
                        loadMore = true,
                        init = false
                    )
                } else {
                    viewModel.searchUser(
                        keyword = inputText.value,
                        loadMore = true,
                        init = false
                    )
                }
            },
            onRetry = {
                if (currentFullSearchType.value == SearchSummaryData.SearchSummaryDataType.THREAD.name) {
                    viewModel.searchThread(
                        keyword = inputText.value,
                        loadMore = it == RetryType.LoadMore,
                        init = it == RetryType.Init
                    )
                } else {
                    viewModel.searchUser(
                        keyword = inputText.value,
                        loadMore = true,
                        init = false
                    )
                }
            },
            listState = listState
        ) { index, item ->
            if (item is CommonThreadEntity) {
                CommonThreadItem(
                    data = item
                )
            } else if (item is SearchSummaryEntity.User) {
                User(
                    data = item,
                )
            }
        }
    }
}