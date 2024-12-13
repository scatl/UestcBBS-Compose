package com.scatl.uestcbbs.compose.module.post

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.rememberMutableStateListOf
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.module.forum.ForumViewModel
import com.scatl.uestcbbs.compose.widget.HorizontalSwitchView
import kotlinx.parcelize.Parcelize

/**
 * Created by tanlei02 at 2024/11/21 9:52:13
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectForumBottomSheet(
    show: MutableState<Boolean>,
    onSelect: (result: SelectForumResult) -> Unit
) {
    if (show.value) {
        val scope = rememberCoroutineScope()
        val viewModel: ForumViewModel = hiltViewModel()
        val context = LocalContext.current
        val listState = rememberLazyListState()
        val showContent2 = rememberSaveable { mutableStateOf(false) }
        val selectedParent = remember { mutableStateOf<IndexEntity.Forum?>(null) }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        fun hide() {
            scope.launchSafety {
                sheetState.hide()
                show.value = false
            }
        }

        LaunchedEffect(showContent2.value) {
            if (showContent2.value.not()) {
                viewModel.resetForumDetail()
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                show.value = false
            },
            sheetState = sheetState,
            sheetGesturesEnabled = false
        ) {

            BackHandler(showContent2.value) {
                showContent2.value = false
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.8f)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = if (showContent2.value) "选择子板块" else "选择板块",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .animateContentSize()
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box {
                    HorizontalSwitchView(
                        showContent2 = showContent2,
                        content1 = {
                            Content1(
                                showContent2 = showContent2,
                                selectedParent = selectedParent
                            )
                        },
                        content2 = {
                            Content2(
                                viewModel = viewModel,
                                selectedParent = selectedParent,
                                onSelect = {
                                    onSelect.invoke(it)
                                    hide()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Content1(
    showContent2: MutableState<Boolean>,
    selectedParent: MutableState<IndexEntity.Forum?>
) {
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ForumCategoryManager.originData.forEach {
            Line(it.name.toString())

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                it.children?.forEach { child ->
                    Text(
                        text = child.name.toString(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = RoundedCornerShape(cardCorner)
                            )
                            .clip(
                                shape = RoundedCornerShape(cardCorner)
                            )
                            .clickable(unbound = false) {
                                if (child.canPostThread == false) {
                                    "你无法在此板块发帖".showToast(context)
                                } else {
                                    showContent2.value = true
                                    selectedParent.value = child
                                }
                            }
                            .padding(horizontal = 5.dp)
                            .alpha(
                                if (child.canPostThread == false) {
                                    0.5f
                                } else {
                                    1f
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Content2(
    viewModel: ForumViewModel,
    selectedParent: MutableState<IndexEntity.Forum?>,
    onSelect: (result: SelectForumResult) -> Unit
) {
    val context = LocalContext.current
    val forumDetailData by viewModel.forumDetailData.collectAsStateWithLifecycle()
    val selectedChildForum = remember { mutableStateOf<IndexEntity.Forum?>(null) }
    val children = rememberMutableStateListOf<IndexEntity.Forum>()
    val categories = rememberMutableStateListOf<ForumDetailEntity.ThreadType>()

    LaunchedEffect(selectedParent.value) {
        children.clear()
        if (selectedParent.value != null) {
            children.add(selectedParent.value!!)
            selectedParent.value?.children?.let { children.addAll(it) }

            if (children.size == 1) {
                selectedChildForum.value = children[0]
                viewModel.getForumDetail(false, selectedChildForum.value?.fid.toIntOrElse())
            }
        }
    }

    LaunchedEffect(forumDetailData.data) {
        categories.clear()
        if (forumDetailData.data?.threadTypes != null) {
            if (forumDetailData.data?.threadTypes.isNullOrEmpty()) {
                categories.add(ForumDetailEntity.ThreadType(
                    name = "不分类",
                    typeId = null
                ))
            }
            forumDetailData.data?.threadTypes?.let { categories.addAll(it) }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Line(selectedParent.value?.name.toString())

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            children.forEach {
                Text(
                    text = it.name.toString(),
                    fontSize = 13.sp,
                    color = if (selectedChildForum.value === it) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier
                        .background(
                            color = if (selectedChildForum.value === it) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            shape = RoundedCornerShape(cardCorner)
                        )
                        .clip(
                            shape = RoundedCornerShape(cardCorner)
                        )
                        .clickable(unbound = false) {
                            if (it.canPostThread == false) {
                                "你无法在此板块发帖".showToast(context)
                            } else {
                                selectedChildForum.value = it
                                viewModel.getForumDetail(false, it.fid.toIntOrElse())
                            }
                        }
                        .padding(horizontal = 5.dp)
                        .alpha(
                            if (it.canPostThread == false) {
                                0.5f
                            } else {
                                1f
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = forumDetailData.data != null
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Line("分类")

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow (
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categories.forEach { category ->
                        Text(
                            text = category.name.toString(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(cardCorner)
                                )
                                .clip(
                                    shape = RoundedCornerShape(cardCorner)
                                )
                                .clickable(unbound = false) {
                                    forumDetailData.data?.let {
                                        onSelect.invoke(
                                            SelectForumResult(
                                                detail = it,
                                                category = category
                                            )
                                        )
                                    }
                                }
                                .padding(horizontal = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Line(text: String) {
    Box {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(10.dp)
                .width(50.dp)
                .padding(start = 2.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), Color.Transparent),
                        start = Offset(0f, 200f),
                        end = Offset(200f, 0f)
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Parcelize
data class SelectForumResult(
    var detail: ForumDetailEntity,
    var category: ForumDetailEntity.ThreadType
): Parcelable