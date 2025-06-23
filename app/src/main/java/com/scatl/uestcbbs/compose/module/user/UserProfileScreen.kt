package com.scatl.uestcbbs.compose.module.user

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.renderscript.Toolkit
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.ext.isGTESdk31
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.module.user.basicinfo.UserBasicInfoScreen
import com.scatl.uestcbbs.compose.module.user.collect.UserCollectionScreen
import com.scatl.uestcbbs.compose.module.user.friend.UserFriendScreen
import com.scatl.uestcbbs.compose.module.user.messageboard.UserMessageBoardScreen
import com.scatl.uestcbbs.compose.module.user.post.UserPostsScreen
import com.scatl.uestcbbs.compose.module.wealth.WaterTransferBottomSheet
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.getBitmapFromUrl
import com.scatl.uestcbbs.compose.util.toArgb8888
import com.scatl.uestcbbs.compose.widget.ArcShape
import com.scatl.uestcbbs.compose.widget.ScrollableTabLayout
import com.scatl.uestcbbs.compose.widget.StatusLayout
import com.scatl.uestcbbs.compose.widget.StickyLayout
import com.scatl.uestcbbs.compose.widget.StickyLayoutController
import com.scatl.uestcbbs.compose.widget.TextInputDialog
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/7/15 16:21:00
 */
@Composable
fun UserProfileScreen(
    routerEntity: Router.UserProfileRouterEntity
) {
    val viewModel: UserViewModel = hiltViewModel<UserViewModel, UserViewModel.Factory> { factory ->
        factory.create(routerEntity.uid.toString(), routerEntity.name)
    }

    val detailData by viewModel.detailData.collectAsStateWithLifecycle()
    val stickyLayoutController = rememberUpdatedState(remember { StickyLayoutController() })
    val progress = rememberSaveable { mutableFloatStateOf(0f) }
    val showWaterTransferBottomSheet = rememberSaveable { mutableStateOf(false) }

    StatusLayout(
        uiState = detailData,
        onRetry = {
            viewModel.getUserProfile()
        }
    ) {
        StickyLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            controller = stickyLayoutController,
            parallaxCoefficient = 0.4f,
            headContent = {
                HeadContent(
                    data = detailData.data,
                    viewModel = viewModel,
                    progress = progress
                )
            },
            barContent = {
                BarContent(
                    data = detailData.data,
                    progress = progress,
                    showWaterTransferBottomSheet = showWaterTransferBottomSheet
                )
            },
            bodyContent = {
                BodyContent(
                    data = detailData.data,
                    viewModel = viewModel,
                    stickyLayoutController = stickyLayoutController,
                    onTabSelected = { index, state ->
                        stickyLayoutController.value.bodyStateChange?.invoke(state)
                    }
                )
            },
            onProgress = { percent, offset ->
                progress.floatValue = percent
            }
        )

        WaterTransferBottomSheet(
            show = showWaterTransferBottomSheet,
            defaultUserName = detailData.data?.userSummary?.username
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarContent(
    data: UserProfileEntity?,
    progress: MutableFloatState,
    showWaterTransferBottomSheet: MutableState<Boolean>,
) {
    if (data == null) {
        return
    }
    val navHostController = LocalNavController.current
    val self = rememberSaveable { mutableStateOf(AccountManager.getSignedInAccount()?.uid == data.userSummary?.uid.toString()) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = progress.floatValue * 3)),
        title = {
            Text(
                text = data.userSummary?.username.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha = progress.floatValue * 3),
                color = lerp(Color.White, MaterialTheme.colorScheme.onBackground, progress.floatValue)
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.content_dsp_user_profile_back),
                tint = lerp(Color.White, MaterialTheme.colorScheme.onBackground, progress.floatValue),
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(30.dp)
                    .unboundClickable {
                        navHostController.popBackStack()
                    }
            )
        },
        actions = {
            Row {
                if (self.value.not()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_money_transfer),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = lerp(Color.White, MaterialTheme.colorScheme.onBackground, progress.floatValue)),
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(35.dp)
                            .padding(5.dp)
                            .unboundClickable {
                                showWaterTransferBottomSheet.value = true
                            }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeadContent(
    data: UserProfileEntity?,
    viewModel: UserViewModel,
    progress: MutableFloatState
) {
    if (data == null) {
        return
    }

    val scope = rememberCoroutineScope()
    val barHeight = TopAppBarDefaults.windowInsets.getTop(LocalDensity.current).px2dp + 64.dp
    var domainColor by rememberSaveable { mutableIntStateOf(0) }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(data.userSummary?.uid?.toAvatarUrl()) {
        iconBitmap = getBitmapFromUrl(context, data.userSummary?.uid?.toAvatarUrl())
    }

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(bottom = 20.dp)
    ) {
        if (iconBitmap != null) {
            AsyncImage(
                model = if (isGTESdk31()) iconBitmap else Toolkit.blur(iconBitmap!!.toArgb8888(), 25),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp + barHeight)
                    .clip(ArcShape(30.dp))
                    .blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
            )
        }

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 160.dp)
                .fillMaxWidth()
        ) {
            //circle avatar
            AsyncImage(
                model = data.userSummary?.uid?.toAvatarUrl(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .border(width = 2.dp, color = Color.White, shape = CircleShape)
                    .shadow(elevation = 10.dp, shape = CircleShape)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (data.userSummary?.groupSubtitle.isNullOrEmpty() && (data.userSummary?.digests ?: 0) <= 0) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = data.userSummary?.username.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = data.userSummary?.groupTitle.toString(),
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        modifier = Modifier
                            .background(
                                color = LocalCustomColors.current.userProfileLevelBg,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(start = 8.dp, end = 8.dp, top = 3.dp, bottom = 2.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            } else {
                Text(
                    text = data.userSummary?.username.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow (
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Text(
                        text = data.userSummary?.groupTitle.toString(),
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        modifier = Modifier
                            .background(
                                color = LocalCustomColors.current.userProfileLevelBg,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(start = 8.dp, end = 8.dp, top = 3.dp, bottom = 2.dp)
                            .align(Alignment.CenterVertically)
                    )
                    if (!data.userSummary?.groupSubtitle.isNullOrEmpty()) {
                        Text(
                            text = data.userSummary?.groupSubtitle.toString(),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(start = 8.dp, end = 8.dp, top = 3.dp, bottom = 2.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    if ((data.userSummary?.digests ?: 0) > 0) {
                        Text(
                            text = stringResource(id = R.string.user_featured_author),
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            LocalCustomColors.current.threadTitleDigestStart,
                                            LocalCustomColors.current.threadTitleDigestEnd
                                        )
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(start = 8.dp, end = 8.dp, top = 3.dp, bottom = 2.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            if (!data.userSummary?.medals.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow (
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(horizontal = pagePadding)
                ) {
                    data.userSummary?.medals?.forEach { id ->
                        val entity = viewModel.userRepository.dataBase.getMedalDao().findFirstById(id)
                        if (entity != null) {
                            AsyncImage(
                                model = entity.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                    }
                }
            }

            if (!data.introduction?.removeAllBlank().isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = data.introduction.toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .alpha(0.6f)
                )
            }

            BtnContent(
                viewModel = viewModel ,
                data = data
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    data.userSummary?.extCredits?.water,
                    data.userSummary?.credits,
                    data.userSummary?.extCredits?.weiWang,
                    data.userSummary?.friends
                ).forEachIndexed { index, i ->
                    Box (
                        modifier = Modifier.weight(1f)
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                text = i.toString(),
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = when(index) {
                                    0 -> stringResource(id = R.string.water)
                                    1 -> stringResource(id = R.string.credits)
                                    2 -> stringResource(id = R.string.prestige)
                                    else -> stringResource(id = R.string.friend)
                                },
                                fontSize = 14.sp,
                                lineHeight = 14.sp,
                                modifier = Modifier.alpha(0.6f)
                            )
                        }
                        if (index < 3) {
                            VerticalDivider(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .height(20.dp)
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun BtnContent(
    viewModel: UserViewModel,
    data: UserProfileEntity?
) {
    if (data == null) {
        return
    }

    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val addFriendData by viewModel.addFriendData.collectAsStateWithLifecycle()
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val self = rememberSaveable { mutableStateOf(AccountManager.getSignedInAccount()?.uid == data.userSummary?.uid.toString()) }

    LaunchedEffect(key1 = addFriendData) {
        if (addFriendData.isSuccess) {
            "已发送好友请求".showToast(context)
        } else if (addFriendData.errorData?.message.isNotNullAndEmpty()) {
            addFriendData.errorData?.message.showToast(context)
        }
    }

    if (self.value.not()) {
        Spacer(modifier = Modifier.height(20.dp))

        if (data.userSummary?.blocked == true) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color.Red
                )
            ) {
                Text(text = stringResource(id = R.string.user_unblock))
            }
        } else {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (data.userSummary?.friendStatus == "requested" || addFriendData.data?.friendStatus == "requested") {
                    Button(
                        onClick = {

                        }
                    ) {
                        Text(text = stringResource(id = R.string.user_friend_request))
                    }
                } else if (data.userSummary?.friendStatus == "friend") {
                    Button(
                        onClick = { }
                    ) {
                        Text(text = stringResource(id = R.string.user_already_friend))
                    }
                } else {
                    Button(
                        onClick = {
                            showDialog.value = true
                        }
                    ) {
                        Text(text = stringResource(id = R.string.user_add_friend))
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = {
                        navHostController.navigate(
                            Router.ChatDetailRouterEntity(
                                uid = data.userSummary?.uid.toIntOrElse(),
                                name = data.userSummary?.username.toString()
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = stringResource(id = R.string.private_message))
                }
            }
        }
    }

    TextInputDialog(
        showDialog = showDialog.value,
        label = stringResource(id = R.string.user_add_friend_dsp),
        icon = Icons.Outlined.PersonAddAlt,
        title = stringResource(id = R.string.user_add_friend),
        onDismissRequest = {
            showDialog.value = false
        },
        onConfirmClick = {
            viewModel.addFriend(it ?: "")
            showDialog.value = false
        }
    )
}

@Composable
private fun BodyContent(
    data: UserProfileEntity?,
    viewModel: UserViewModel,
    stickyLayoutController: State<StickyLayoutController>,
    onTabSelected: (Int, ScrollableState) -> Unit = { _, _ -> }
) {
    if (data == null) {
        return
    }

    val pageHomeScrollState = rememberScrollState()
    val pagePostState = rememberLazyListState()
    val pageCollectionState = rememberLazyListState()
    val pageFriendState = rememberLazyListState()
    val pageMsgBoardState = rememberLazyListState()

    val homeTitle = stringResource(id = R.string.home)
    val postTitle = stringResource(id = R.string.post)
    val favoriteTitle = stringResource(id = R.string.favorite)
    val friendTitle = stringResource(id = R.string.friend)
    val messageBoardTitle = stringResource(id = R.string.message_board)

    val states = remember {
        mutableStateListOf(pageHomeScrollState, pagePostState)
    }
    val titles = remember(
        data.userSummary?.favoritesUnavailable,
        data.userSummary?.commentsHidden,
        data.userSummary?.friendsHidden
    ) {
        mutableListOf(
            Pair(homeTitle, UserProfilePage.HOME),
            Pair(postTitle, UserProfilePage.POST),
        ).apply {
            if (data.userSummary?.favoritesUnavailable == false) {
                add(Pair(favoriteTitle, UserProfilePage.COLLECTION))
                states.add(pageCollectionState)
            }
            if (data.userSummary?.friendsHidden == false) {
                add(Pair(friendTitle, UserProfilePage.FRIEND))
                states.add(pageFriendState)
            }
            if (data.userSummary?.commentsHidden == false) {
                add(Pair(messageBoardTitle, UserProfilePage.MESSAGE_BOARD))
                states.add(pageMsgBoardState)
            }
        }
    }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        pageCount = { titles.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onTabSelected(page, states[page])
        }
    }

    Column {
        ScrollableTabLayout(
            tabs = titles.map { it.first },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface),
            selectTabStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 17.sp
            ),
            unSelectTabStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp
            ),
            tabClick = {
                scope.launch {
                    pagerState.animateScrollToPage(it)
                }
            },
            pagerState = pagerState
        )

        HorizontalPager(
            state = pagerState,
            key = { index -> index }
        ) { page ->
            when (titles[page].second) {
                UserProfilePage.HOME -> {
                    UserBasicInfoScreen(
                        data = data,
                        viewModel = viewModel,
                        state = pageHomeScrollState,
                    )
                }
                UserProfilePage.POST -> {
                    UserPostsScreen(
                        data = data,
                        viewModel = viewModel,
                        state = pagePostState,
                    )
                }
                UserProfilePage.COLLECTION -> {
                    UserCollectionScreen(
                        data = data,
                        viewModel = viewModel,
                        state = pageCollectionState,
                    )
                }
                UserProfilePage.FRIEND -> {
                    UserFriendScreen(
                        data = data,
                        viewModel = viewModel,
                        state = pageFriendState,
                    )
                }
                UserProfilePage.MESSAGE_BOARD -> {
                    UserMessageBoardScreen(
                        stickyLayoutController = stickyLayoutController,
                        data = data,
                        viewModel = viewModel,
                        state = pageMsgBoardState,
                    )
                }
            }
        }
    }

}

enum class UserProfilePage {
    HOME, POST, COLLECTION, MESSAGE_BOARD, FRIEND
}
