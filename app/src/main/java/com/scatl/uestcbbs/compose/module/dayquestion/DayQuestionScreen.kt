package com.scatl.uestcbbs.compose.module.dayquestion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.TIP_ID_DAY_QUESTION
import com.scatl.uestcbbs.compose.widget.Tip
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2025/3/21 17:27:14
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayQuestionScreen() {
    val viewModel: DayQuestionViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val dayQuestions by viewModel.dayQuestionData.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val showAddDialog = rememberSaveable { mutableStateOf(false) }

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            viewModel.getDayQuestions()
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
                        text = "自动答题"
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
                                showAddDialog.value = true
                            }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(paddingValues)
        ) {
            Tip(
                tipId = TIP_ID_DAY_QUESTION,
                tip = "下面展示的是自动答题使用到的本地题库，如果遇到答题时提示“没有找到答案，请先手动记录”，你可以点击右上角加号手动向题库添加题目与答案。"
            )
            SwipeRefresh(
                uiState = dayQuestions,
                modifier = Modifier
                    .fillMaxSize(),
                listState = listState,
                enableLoadMore = false,
                enableRefresh = false,
            ) { _, item ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .commonCardBg {  }
                ) {
                    Column (
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Row {
                            Text(
                                text = "Q：",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = item.question,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row {
                            Text(
                                text = "A：",
                                modifier = Modifier
                                    .alpha(0.8f)
                            )
                            Text(
                                text = item.answer,
                                modifier = Modifier
                                    .alpha(0.8f)
                            )
                        }
                    }
                    if (item.deletable) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(end = pagePadding)
                                .size(20.dp)
                                .clickable(unbound = true) {
                                    viewModel.dayQuestionRepository.dataBase.getDayQuestionDao().deleteByQuestion(item.question)
                                    viewModel.getDayQuestions()
                                    "删除成功".showToast(context)
                                }
                        )
                    }
                }
            }
        }
    }

    AddQuestionDialog(
        showAddDialog = showAddDialog,
        viewModel = viewModel
    )
}

@Composable
fun AddQuestionDialog(
    showAddDialog: MutableState<Boolean>,
    viewModel: DayQuestionViewModel
) {
    if (showAddDialog.value) {
        val focusRequester = remember { FocusRequester() }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val question = rememberSaveable { mutableStateOf("") }
        val answer = rememberSaveable { mutableStateOf("") }

        LaunchedEffect(Unit) {
            scope.launchSafety {
                delay(100)
                focusRequester.requestFocus()
            }
        }

        Dialog(
            onDismissRequest = {
                showAddDialog.value = false
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 30.dp, horizontal = 20.dp),
            ) {
                IconTitle(
                    icon = Icons.Outlined.Add,
                    iconSize = 24.dp,
                    text = "添加问题与答案",
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(
                    value = question.value.removeAllBlank().toString(),
                    onValueChange = {
                        question.value = it
                    },
                    label = { Text(text = "请输入问题") },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = answer.value.removeAllBlank().toString(),
                    onValueChange = {
                        answer.value = it
                    },
                    label = { Text(text = "请输入答案") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Box (
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(enabled = true) {
                                if (question.value.isNotNullAndEmpty() && answer.value.isNotNullAndEmpty()) {
                                    viewModel.dayQuestionRepository.dataBase.getDayQuestionDao().insert(
                                        DayQuestionDBEntity(
                                            id = 0,
                                            question = question.value,
                                            answer = answer.value,
                                            deletable = true
                                        )
                                    )
                                    viewModel.getDayQuestions()
                                    "添加成功".showToast(context)
                                    showAddDialog.value = false
                                }
                            }
                    )
                }
            }
        }
    }
}