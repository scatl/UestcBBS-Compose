package com.scatl.uestcbbs.compose.module.user.mine

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.UserLevel
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.isGTESdk31
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.theme.RevealAnimModel
import com.scatl.uestcbbs.compose.manager.ThemeManager
import com.scatl.uestcbbs.compose.theme.activeRevealView
import com.scatl.uestcbbs.compose.module.user.UserViewModel
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.util.calculateDays
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.IconPosition
import com.scatl.uestcbbs.compose.widget.IconTitle
import kotlinx.coroutines.launch

/**
 * Created by sca_tl at 2024/7/15 16:36:00
 */
@Composable
fun MineScreen() {
    val signedInAccount by AccountManager.signedInAccount.collectAsState()
    val viewModel: UserViewModel = hiltViewModel<UserViewModel, UserViewModel.Factory> { factory ->
        factory.create(signedInAccount?.uid, signedInAccount?.name)
    }
    val detailData by viewModel.detailData.collectAsStateWithLifecycle()

    Box {
        BlurBg()

        Box (
            modifier = Modifier
                .systemBarsPadding()
                .padding(top = 20.dp)
        ) {
            TopIcon()

            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    //todo 下面两个会冲突
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, _, _ ->
                            SharedFlowBus
                                .with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY)
                                .tryEmit(BaseEvent.MainNavVisibleEvent(true))
                        }
                    }
            ) {
                if (signedInAccount == null) {
                    NotSignedView()
                } else {
                    SignedView(
                        signedInAccount = signedInAccount!!,
                        data = detailData.data
                    )
                }

                BBSTools()
                MoreTools()
            }
        }
    }
}

@Composable
private fun SignedView(
    signedInAccount: AccountDBEntity,
    data: UserProfileEntity?
) {
    val navHostController = LocalNavController.current
    val nextLevel = rememberUpdatedState(
        newValue = UserLevel.getNextLevelByScore(data?.userSummary?.credits)
    )
    val levelProgress by animateFloatAsState(
        targetValue = if (nextLevel.value == null) {
            0f
        } else if (data?.userSummary?.credits != null && nextLevel.value?.minScore != null) {
            data.userSummary.credits.toFloat() / nextLevel.value!!.minScore.toFloat()
        } else {
            0f
        },
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "level_progress"
    )

    Column (
        modifier = Modifier
            .systemBarsPadding()
            .padding(top = 20.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                AsyncImage(
                    model = data?.userSummary?.uid.toAvatarUrl(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(shape = RoundedCornerShape(50))
                )
                Column {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = data?.userSummary?.username.toString(),
                            fontSize = 18.sp
                        )
                        if (!data?.userSummary?.groupTitle.toString().contains("Lv")) {
                            Text(
                                text = data?.userSummary?.groupTitle.toString(),
                                fontSize = 10.sp,
                                lineHeight = 10.sp,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .padding(
                                        start = 4.dp,
                                        end = 4.dp,
                                        top = 3.dp,
                                        bottom = 2.dp
                                    )
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = stringResource(R.string.mine_join_days, calculateDays(data?.registerTime)),
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.alpha(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            IconTitle(
                icon = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                iconSize = 20.dp,
                iconPosition = IconPosition.RIGHT,
                text = stringResource(R.string.mine_self_profile),
                gap = 0.dp,
                textStyle = TextStyle(),
                modifier = Modifier
                    .unboundClickable {
                        navHostController.navigate(
                            Router.UserProfileRouterEntity(
                                uid = signedInAccount.uid.toIntOrElse(),
                                name = signedInAccount.name.toString()
                            )
                        )
                    }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row (
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(shape = RoundedCornerShape(cardCorner * 2))
                    .border(
                        width = 0.5.dp,
                        color = LocalCustomColors.current.meLevelCardBg,
                        shape = RoundedCornerShape(cardCorner * 2)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(levelProgress)
                        .background(
                            color = LocalCustomColors.current.meLevelCardBg,
                        )
                        .padding(pagePadding)
                )

                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pagePadding)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.StackedLineChart,
                        contentDescription = null,
                        tint = LocalCustomColors.current.meLevelCardDsp.copy(alpha = 0.05f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(y = 30.dp, x = 25.dp)
                            .size(80.dp)
                    )
                    Column (
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.user_level),
                            fontSize = 15.sp,
                            lineHeight = 15.sp
                        )
                        Text(
                            text = "Lv.${data?.userSummary?.levelId}",
                            fontSize = 17.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.mine_next_level_score, (nextLevel.value?.minScore.toIntOrElse() - data?.userSummary?.credits.toIntOrElse()).toString()),
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            color = LocalCustomColors.current.meLevelCardDsp
                        )
                    }
                }
            }

            Box (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = LocalCustomColors.current.meWaterCardBg,
                        shape = RoundedCornerShape(cardCorner * 2)
                    )
                    .clip(shape = RoundedCornerShape(cardCorner * 2))
                    .padding(pagePadding)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    tint = LocalCustomColors.current.meWaterCardDsp.copy(alpha = 0.05f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = 30.dp, x = 25.dp)
                        .size(80.dp)
                )
                Column (
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.water),
                        fontSize = 15.sp,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = data?.userSummary?.extCredits?.water.toString(),
                        fontSize = 17.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.mine_water_dsp),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = LocalCustomColors.current.meWaterCardDsp
                    )
                }

            }
        }
    }
}

@Composable
private fun NotSignedView() {
    val navHostController = LocalNavController.current
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                navHostController.navigate(Router.AccountManageRouterEntity) {
                    popUpTo(Router.MainRouterEntity) {
                        inclusive = true
                    }
                }
            }
        ) {
            Text(text = stringResource(R.string.login))
        }
    }
}

@Composable
private fun BlurBg() {
    if (isGTESdk31()) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .blur(radius = 100.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .background(
                        color = Color.Green.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
                    .width(100.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(50))
            )

            Box(
                modifier = Modifier
                    .padding(start = 80.dp)
                    .background(
                        color = Color.Blue.copy(alpha = 0.3f)
                    )
                    .width(100.dp)
                    .height(100.dp)

            )

            Box(
                modifier = Modifier
                    .padding(top = 50.dp, start = 70.dp)
                    .align(Alignment.Center)
                    .background(
                        color = Color.Cyan.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
                    .width(150.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(50))
            )

            Box(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color.Magenta.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
                    .width(100.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun TopIcon() {
    val dayNightMode by ThemeManager.dayNightMode.collectAsState()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val scope = rememberCoroutineScope()
    var themeSwitchPositionX by remember { mutableFloatStateOf(0f) }
    var themeSwitchPositionY by remember { mutableFloatStateOf(0f) }
    val changeThemeEnable = remember { mutableStateOf(true) }
    val showChangeThemeDialog = rememberSaveable { mutableStateOf(false) }

    fun changeTheme() {
        changeThemeEnable.value = false
        context.activeRevealView(
            animModel = if (ThemeManager.isAppDarkMode) RevealAnimModel.SHRINK else RevealAnimModel.EXPEND,
            clickX = themeSwitchPositionX,
            clickY = themeSwitchPositionY,
            animTime = 700,
            createRevealComplete = {
                scope.launch {
                    if (ThemeManager.isAppDarkMode) {
                        ThemeManager.toggleDayNightMode(ThemeManager.DayNightMode.DAY)
                        ThemeManager.toggleAppNightMode(false)
                    } else {
                        ThemeManager.toggleDayNightMode(ThemeManager.DayNightMode.NIGHT)
                        ThemeManager.toggleAppNightMode(true)
                    }
                }
            },
            revealAnimFinish = {
                changeThemeEnable.value = true
            },
        )
    }

    Row (
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp)
    ) {
        Icon(
            imageVector = when (dayNightMode) {
                ThemeManager.DayNightMode.NIGHT.value -> {
                    Icons.Outlined.LightMode
                }
                ThemeManager.DayNightMode.DAY.value -> {
                    Icons.Outlined.DarkMode
                }
                else -> {
                    if (ThemeManager.isAppDarkMode) {
                        Icons.Outlined.LightMode
                    } else {
                        Icons.Outlined.DarkMode
                    }
                }
            },
            contentDescription = null,
            modifier = Modifier
                .alpha(alpha = 0.6f)
                .unboundClickable {
                    if (!changeThemeEnable.value) {
                        return@unboundClickable
                    }
                    if (dayNightMode == ThemeManager.DayNightMode.FOLLOW_SYSTEM.value) {
                        showChangeThemeDialog.value = true
                    } else {
                        changeTheme()
                    }
                }
                .onGloballyPositioned { coordinates ->
                    themeSwitchPositionX = coordinates.boundsInRoot().center.x
                    themeSwitchPositionY = coordinates.boundsInRoot().center.y
                }
        )

        Spacer(modifier = Modifier.width(20.dp))

        Icon(
            imageVector = Icons.Outlined.ManageAccounts,
            contentDescription = null,
            modifier = Modifier
                .alpha(alpha = 0.6f)
                .unboundClickable {
                    navHostController.navigate(Router.AccountManageRouterEntity)
                }
        )

        Spacer(modifier = Modifier.width(20.dp))

        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = null,
            modifier = Modifier
                .alpha(alpha = 0.6f)
                .unboundClickable {
                    navHostController.navigate(Router.SettingRouterEntity)
                }
        )
    }

    CommonAlertDialog(
        showDialog = showChangeThemeDialog.value,
        title = stringResource(R.string.mine_change_theme),
        text = stringResource(R.string.mine_change_theme_dsp),
        onDismissRequest = {
            showChangeThemeDialog.value = false
        },
        onConfirmClick = {
            showChangeThemeDialog.value = false
            changeTheme()
        }
    )
}

@Composable
private fun BBSTools() {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .padding(horizontal = pagePadding * 2)
            .padding(top = pagePadding * 2)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                shape = RoundedCornerShape(cardCorner * 2)
            )
            .padding(pagePadding)
    ) {
        Text(
            text = stringResource(R.string.mine_bbs_tool_title),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .alpha(0.6f)
                .padding(start = 5.dp, top = 5.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(60.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp, max = 1000.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            state = rememberLazyGridState(),
            userScrollEnabled = false
        ) {
            item {
                BBSToolItem(
                    resId = R.drawable.ic_water_task,
                    color = LocalCustomColors.current.meWaterTask,
                    text = stringResource(R.string.water_task_title)
                ) {
                    navHostController.navigate(Router.WaterTaskRouterEntity)
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_magic_shop,
                    color = LocalCustomColors.current.meMagicShop,
                    text = stringResource(R.string.magic_shop_title)
                ) {
                    navHostController.navigate(Router.MagicShopRouterEntity)
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_medal,
                    color = LocalCustomColors.current.meMedalCenter,
                    text = stringResource(R.string.medal_center_title)
                ) {
                    navHostController.navigate(Router.MedalRouterEntity)
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_collection,
                    color = LocalCustomColors.current.meCollection,
                    text = stringResource(R.string.collection)
                ) {
                    navHostController.navigate(Router.CollectionListRouterEntity())
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_wealth,
                    color = LocalCustomColors.current.meWealth,
                    text = "我的财富"
                ) {
                    navHostController.navigate(Router.MyWealthRouterEntity)
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_black_list,
                    color = LocalCustomColors.current.meCreditHistory,
                    text = "小黑屋"
                ) {
                    navHostController.navigate(Router.DarkRoomRouterEntity)
                }
            }
        }
    }

}

@Composable
private fun MoreTools() {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .padding(pagePadding * 2)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                shape = RoundedCornerShape(cardCorner * 2)
            )
            .padding(pagePadding)
    ) {
        Text(
            text = stringResource(R.string.mine_more_features),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .alpha(0.6f)
                .padding(start = 5.dp, top = 5.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(60.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp, max = 1000.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            state = rememberLazyGridState(),
            userScrollEnabled = false
        ) {
            item {
                BBSToolItem(
                    resId = R.drawable.ic_history,
                    color = LocalCustomColors.current.meBrowserHistory,
                    text = stringResource(R.string.browsing_history_title)
                ) {
                    navHostController.navigate(Router.HistoryRouterEntity)
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_snapshot,
                    color = LocalCustomColors.current.meSnapshot,
                    text = stringResource(R.string.snapshot_title)
                ) {
                    navHostController.navigate(Router.SnapshotRouterEntity)
                }
            }

            item {
                BBSToolItem(
                    resId = R.drawable.ic_snapshot,
                    color = LocalCustomColors.current.meSnapshot,
                    text = "自动答题"
                ) {
                    navHostController.navigate(Router.DayQuestionRouterEntity)
                }
            }
        }
    }

}

@Composable
private fun BBSToolItem(resId: Int, color: Color, text: String, onClick: () -> Unit) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clickable(unbound = true) {
                onClick.invoke()
            }
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = color),
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
    }
}