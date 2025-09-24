package com.scatl.uestcbbs.compose.module.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.widget.ScrollableTabLayout
import com.scatl.uestcbbs.compose.widget.TabIndicatorType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.router.LocalNavController

/**
 * Created by sca_tl at 2024/10/10 18:01:12
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    create: Boolean = false
) {
    val tag = "CollectionScreen"
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val openCreateCollectionBottomSheet = rememberSaveable { mutableStateOf(false) }

    LoadInitialDataIfNeeded(create) {
        scope.launch {
            delay(500)
            openCreateCollectionBottomSheet.value = create
        }
    }

    Column (
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .navigationBarsPadding()
    ) {
        LargeTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            scrollBehavior = scrollBehavior,
            title = {
                Text(
                    text = stringResource(R.string.collection)
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
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = pagePadding)
                        .size(30.dp)
                        .clickable(unbound = true) {
                            openCreateCollectionBottomSheet.value = true
                        }
                )
            }
        )

        Content()
    }

    CreateCollectionBottomSheet(
        openCreateCollectionBottomSheet = openCreateCollectionBottomSheet
    )
//    Scaffold (
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            LargeTopAppBar(
//                colors = TopAppBarDefaults.largeTopAppBarColors().copy(
//                    containerColor = MaterialTheme.colorScheme.surfaceContainer
//                ),
//                scrollBehavior = scrollBehavior,
//                title = {
//                    Text(
//                        text = "专辑"
//                    )
//                },
//                navigationIcon = {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(start = 5.dp)
//                            .size(30.dp)
//                            .unboundClickable {
//                                navHostController.popBackStack()
//                            }
//                    )
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column (
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    color = MaterialTheme.colorScheme.surfaceContainer
//                )
//                .padding(paddingValues)
//        ) {
//            Content(
//                navHostController = navHostController
//            )
//        }
//    }
}

@Composable
private fun Content() {
    val titles = remember { mutableStateListOf<String>() }
    if (titles.isEmpty()) {
        titles.add(stringResource(R.string.collection_my))
        titles.add(stringResource(R.string.collection_recommend))
        titles.add(stringResource(R.string.collection_all))
    }

    val pagerState = rememberPagerState(
        pageCount = { titles.size }
    )

    Tab(
        titles = titles,
        pagerState = pagerState
    )

    HorizontalPager(
        state = pagerState,
        key = { index -> index }
    ) { page ->
        CollectionListScreen(
            collectionType = when(page) {
                0 -> CollectionType.MINE
                1 -> CollectionType.RECOMMEND
                else -> CollectionType.ALL
            }
        )
    }
}

@Composable
private fun Tab(
    titles: List<String>,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope()

    ScrollableTabLayout(
        tabs = titles,
        dotCounts = null,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        indicatorType = TabIndicatorType.OVAL,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(bottom = pagePadding, start = pagePadding)
            .padding(end = pagePadding),
        selectTabStyle = TextStyle(
            color = Color.White,
            fontSize = 15.sp,
        ),
        unSelectTabStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp
        ),
        tabClick = {
            scope.launch {
                pagerState.animateScrollToPage(it)
            }
        },
        pagerState = pagerState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionBottomSheet(
    openCreateCollectionBottomSheet: MutableState<Boolean>,
    onCreateSuccess: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: CollectionViewModel = hiltViewModel()
    val createCollectionData by viewModel.createCollectionData.collectAsStateWithLifecycle()
    val createCollectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val collectionName = rememberSaveable { mutableStateOf("") }
    val collectionDsp = rememberSaveable { mutableStateOf("") }
    val collectionTags = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(createCollectionData) {
        if (createCollectionData.data != null) {
            if (createCollectionData.isSuccess) {
                ContextCompat
                    .getContextForLanguage(context)
                    .getString(R.string.collection_create_success, collectionName.value)
                    .showToast(context)
                createCollectionSheetState.hide()
                openCreateCollectionBottomSheet.value = false
                onCreateSuccess?.invoke()
            } else {
                (createCollectionData.errorData?.message
                    ?: ContextCompat.getString(context, R.string.collection_create_fail)
                        ).showToast(context)
            }
            viewModel.resetCreateCollectionData()
        }
    }

    if (openCreateCollectionBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openCreateCollectionBottomSheet.value = false
            },
            sheetState = createCollectionSheetState
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.collection_create_title)
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = collectionName.value,
                    onValueChange = {
                        collectionName.value = it
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.collection_create_name)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = collectionDsp.value,
                    onValueChange = {
                        collectionDsp.value = it
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.collection_create_dsp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = collectionTags.value,
                    onValueChange = {
                        collectionTags.value = it
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.collection_create_tags)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.collection_create_tags_dsp),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    enabled = collectionName.value.isNotNullAndEmpty(),
                    onClick = {
                        viewModel.createCollection(
                            title = collectionName.value,
                            desc = collectionDsp.value,
                            keyword = collectionTags.value
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.create),
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )
                }
            }
        }
    }
}

enum class CollectionType(val type: String) {
    MINE("my"),
    RECOMMEND("recommend"),
    ALL("all")
}

enum class CollectionOrder(val order: String) {
    FOLLOW_NUM("follownum"),
    CREATE_TIME("createtime"),
    THREAD_NUM("threadnum"),
    COMMENT_NUM("commentnum")
}