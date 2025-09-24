package com.scatl.uestcbbs.compose.module.darkroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.router.linkNavigate
import com.scatl.uestcbbs.compose.theme.DarkTheme
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.RegexUtil
import com.scatl.uestcbbs.compose.widget.AuthorLabel
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.SwipeRefresh
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2025/6/4 19:55:18
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkRoomScreen() {
    val viewModel: DarkRoomViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val darkRoomListData by viewModel.darkRoomListData.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val navHostController = LocalNavController.current

    LoadInitialDataIfNeeded(context) {
        scope.launchSafety {
            delay(300)
            viewModel.getDarkRoomList(init = true, loadMore = false)
        }
    }

    DarkTheme {
        Scaffold (
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = "小黑屋"
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
                SwipeRefresh(
                    uiState = darkRoomListData,
                    modifier = Modifier
                        .fillMaxSize(),
                    listState = listState,
                    onRefresh = {
                        viewModel.getDarkRoomList(
                            loadMore = false,
                            init = false
                        )
                    },
                    onRetry = {
                        viewModel.getDarkRoomList(
                            loadMore = it != RetryType.Init,
                            init = it == RetryType.Init
                        )
                    },
                    onLoadMore = {
                        viewModel.getDarkRoomList(
                            loadMore = true,
                            init = false
                        )
                    }
                ) { _, item ->
                    Column(
                        modifier = Modifier
                            .commonCardBg()
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .clickable(unbound = true) {
                                    navHostController.navigate(
                                        Router.UserProfileRouterEntity(
                                            uid = item.uid.toIntOrElse(),
                                            name = item.username.toString()
                                        )
                                    )
                                }
                        ) {
                            AsyncImage(
                                model = item.uid.toIntOrElse().toAvatarUrl(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(shape = RoundedCornerShape(50))
                            )

                            Text(
                                text = item.username.toString(),
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .alpha(0.8f)
                            )
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "操作行为：",
                                fontSize = 14.sp,
                            )
                            Text(
                                text = item.action.toString(),
                                fontSize = 14.sp,
                                color = if (item.action?.contains("禁止发言") == true) {
                                    LocalCustomColors.current.darkRoomWarning
                                } else if (item.action?.contains("禁止访问") == true) {
                                    LocalCustomColors.current.darkRoomError
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }

                        Text(
                            text = "过期时间：".plus(item.groupexpiry.toString()),
                            fontSize = 14.sp,
                        )

                        Text(
                            text = buildAnnotatedString {
                                val s = "操作理由：" + item.reason.toString().replace("\n", "")
                                val urlMatchResult = RegexUtil.matchUrl(s)
                                append(s)

                                if (urlMatchResult.isEmpty().not()) {
                                    urlMatchResult.forEach { res ->
                                        addLink(
                                            LinkAnnotation.Clickable(
                                                tag = "subject",
                                                styles = TextLinkStyles(
                                                    style = SpanStyle(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        textDecoration = TextDecoration.Underline
                                                    )
                                                ),
                                                linkInteractionListener = {
                                                    linkNavigate(
                                                        url = res.value,
                                                        navHostController = navHostController,
                                                        uriHandler = uriHandler
                                                    )
                                                }
                                            ),
                                            start = res.range.first,
                                            end = res.range.last
                                        )
                                    }
                                }
                            },
                            fontSize = 14.sp,
                        )

                        Text(
                            text = "操作时间：" + AnnotatedString.fromHtml(
                                item.dateline.toString()
                            ),
                            fontSize = 14.sp,
                        )

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 5.dp)
                        ) {
                            Text(
                                text = "操作人：",
                                fontSize = 14.sp,
                            )
                            AuthorLabel(
                                uid = item.operatorid.toIntOrElse(),
                                name = item.operator,
                                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}