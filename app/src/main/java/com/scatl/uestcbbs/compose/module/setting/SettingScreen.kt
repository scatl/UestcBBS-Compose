package com.scatl.uestcbbs.compose.module.setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.getVersionName
import com.scatl.uestcbbs.compose.ext.goToAppNotificationChannelSetting
import com.scatl.uestcbbs.compose.ext.goToAppNotificationSetting
import com.scatl.uestcbbs.compose.ext.hexToColor
import com.scatl.uestcbbs.compose.ext.isGTESdk31
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.manager.LanguageManager
import com.scatl.uestcbbs.compose.manager.ThemeManager
import com.scatl.uestcbbs.compose.module.dayquestion.DayQuestionService
import com.scatl.uestcbbs.compose.module.download.DownloadTask
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.SingleSelectionDialog

/**
 * Created by sca_tl at 2024/6/17 16:58:32
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val scrollState = rememberLazyListState()
    val topBarHeight = rememberSaveable { mutableIntStateOf(0) }
    val dynamicTheme= rememberSaveable { mutableStateOf(DataStore.dynamicTheme) }
    val interfaceLanguage = rememberSaveable { mutableStateOf(DataStore.interfaceLanguage) }
    val customTheme = rememberSaveable { mutableStateOf(DataStore.customTheme) }
    val hideDailyPic = rememberSaveable { mutableStateOf(DataStore.hideDailyPic) }
    val ignoreSSL = rememberSaveable { mutableStateOf(DataStore.ignoreSSL) }
    val videoAutoPlay = rememberSaveable { mutableStateOf(DataStore.videoAutoPlay) }
    val autoDayQuestion = rememberSaveable { mutableStateOf(DataStore.autoDayQuestion) }

    val showInterfaceLanguageDialog = rememberSaveable { mutableStateOf(false) }
    val showDayNightModeDialog = rememberSaveable { mutableStateOf(false) }
    val showColorPickerDialog = rememberSaveable { mutableStateOf(false) }

    val cacheSize = viewModel.cacheSize.collectAsStateWithLifecycle()

    val alpha by remember {
        derivedStateOf {
            when {
                scrollState.layoutInfo.visibleItemsInfo.isNotEmpty() && scrollState.firstVisibleItemIndex == 0 -> {
                    val item0Size = scrollState.layoutInfo.visibleItemsInfo[0].size
                    val scrollOffset = scrollState.firstVisibleItemScrollOffset
                    scrollOffset / (item0Size.toFloat() - topBarHeight.intValue)
                }
                else -> 1f
            }
        }
    }

    val firstItemTranslationY by remember {
        derivedStateOf {
            when {
                scrollState.layoutInfo.visibleItemsInfo.isNotEmpty() && scrollState.firstVisibleItemIndex == 0 -> {
                    scrollState.firstVisibleItemScrollOffset * 0.5f
                }
                else -> 0f
            }
        }
    }

    LoadInitialDataIfNeeded(context) {
        viewModel.getCacheSize()
    }

    key(interfaceLanguage.value) {
        Scaffold(
            content = { paddingValues ->
                Box(
                    contentAlignment = Alignment.TopCenter
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(
                                bottom = paddingValues.calculateBottomPadding()
                            ),
                        state = scrollState
                    ) {
                        //top image
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 15.dp,
                                        end = 10.dp,
                                        top = paddingValues.calculateTopPadding(),
                                        bottom = 40.dp
                                    )
                                    .graphicsLayer {
                                        translationY = firstItemTranslationY
                                    },
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_title),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(bottom = 20.dp)
                                        .alpha(1 - alpha * 4)
                                )

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(R.drawable.setting_screen_img).build(),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .fillMaxWidth(0.55f),
                                    contentDescription = null
                                )
                            }
                        }

                        //appearance
                        stickyHeader {
                            Text(
                                text = stringResource(id = R.string.setting_appearance_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                            )
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .clipToBounds()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        showInterfaceLanguageDialog.value = true
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_interface_language),
                                    fontSize = 15.sp,
                                )
                                DspText(text = stringResource(id = R.string.setting_interface_language_dsp))
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.setting_dynamic_color),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(id = R.string.setting_dynamic_color_dsp))
                                }

                                Spacer(modifier = Modifier.width(20.dp))
                                Switch(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    checked = dynamicTheme.value && customTheme.value.isEmpty(),
                                    enabled = isGTESdk31(),
                                    onCheckedChange = {
                                        if (it) {
                                            ThemeManager.toggleCustomTheme("")
                                            customTheme.value = ""
                                        }
                                        ThemeManager.toggleUseDynamicColor(it)
                                        dynamicTheme.value = it
                                    }
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .clipToBounds()
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        showColorPickerDialog.value = true
                                    }
                                    .padding(horizontal = 15.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.setting_custom_theme),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(id = R.string.setting_custom_theme_dsp))
                                }

                                Spacer(modifier = Modifier.width(20.dp))
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(
                                            color = if (customTheme.value.isEmpty()) {
                                                Color.Transparent
                                            } else {
                                                animateColorAsState(
                                                    targetValue = rememberDynamicColorScheme(
                                                        seedColor = customTheme.value.hexToColor(),
                                                        style = PaletteStyle.entries.find { it.name == DataStore.customThemeScheme } ?: PaletteStyle.Fidelity,
                                                        isDark = ThemeManager.isAppDarkMode,
                                                    ).primary,
                                                    animationSpec = tween(durationMillis = 500),
                                                    label = "customThemeColor"
                                                ).value
                                            },
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        showDayNightModeDialog.value = true
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.setting_dark_mode),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(id = R.string.setting_dark_mode_dsp))
                                }
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.setting_hide_daily_pic),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(id = R.string.setting_hide_daily_pic_dsp))
                                }

                                Spacer(modifier = Modifier.width(20.dp))
                                Switch(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    checked = hideDailyPic.value,
                                    onCheckedChange = {
                                        hideDailyPic.value = it
                                        DataStore.hideDailyPic = it
                                    }
                                )
                            }
                        }

                        //browser
                        stickyHeader {
                            Text(
                                text = stringResource(R.string.setting_function_title),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(R.string.setting_video_auto_play),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(R.string.setting_video_auto_play_dsp))
                                }

                                Spacer(modifier = Modifier.width(20.dp))
                                Switch(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    checked = videoAutoPlay.value,
                                    onCheckedChange = {
                                        DataStore.videoAutoPlay = it
                                        videoAutoPlay.value = it
                                    }
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(R.string.setting_auto_day_question),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(R.string.setting_auto_day_question_dsp))
                                }

                                Spacer(modifier = Modifier.width(20.dp))
                                Switch(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    checked = autoDayQuestion.value,
                                    onCheckedChange = {
                                        DataStore.autoDayQuestion = it
                                        autoDayQuestion.value = it
                                    }
                                )
                            }
                        }

                        //network
                        stickyHeader {
                            Text(
                                text = stringResource(id = R.string.setting_network_title),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.setting_ignore_ssl),
                                        fontSize = 15.sp
                                    )
                                    DspText(text = stringResource(id = R.string.setting_ignore_ssl_dsp))
                                }

                                Spacer(modifier = Modifier.width(20.dp))
                                Switch(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    checked = ignoreSSL.value,
                                    onCheckedChange = {
                                        DataStore.ignoreSSL = it
                                        ignoreSSL.value = it
                                    }
                                )
                            }
                        }

                        //notification
                        stickyHeader {
                            Text(
                                text = stringResource(R.string.notification),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                            )
                        }
                        item {
                            Column (
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        goToAppNotificationSetting(context)
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(R.string.setting_notification_manage),
                                    fontSize = 15.sp
                                )
                                DspText(text = stringResource(R.string.setting_notification_manage_dsp))
                            }
                        }
                        item {
                            Column (
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        goToAppNotificationChannelSetting(context, DayQuestionService.CHANNEL_ID.toString())
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(R.string.setting_notification_day_question),
                                    fontSize = 15.sp
                                )
                                DspText(text = stringResource(R.string.setting_notification_day_question_dsp))
                            }
                        }
                        item {
                            Column (
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        goToAppNotificationChannelSetting(context, DownloadTask.CHANNEL_ID)
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(R.string.setting_notification_download),
                                    fontSize = 15.sp
                                )
                                DspText(text = stringResource(R.string.setting_notification_download_dsp))
                            }
                        }

                        //other
                        stickyHeader {
                            Text(
                                text = stringResource(id = R.string.setting_other_title),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.surface)
                                    .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
                            )
                        }
                        item {
                            Column (
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {

                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_check_update),
                                    fontSize = 15.sp
                                )
                                DspText(text = stringResource(id = R.string.setting_check_update_dsp, getVersionName(context)))
                            }
                        }
                        item {
                            Column (
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        viewModel.deleteCache()
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_clear_cache),
                                    fontSize = 15.sp
                                )
                                DspText(text = stringResource(id = R.string.setting_clear_cache_dsp, cacheSize.value))
                            }
                        }
                        item {
                            Column (
                                modifier = Modifier
                                    .clipToBounds()
                                    .wrapContentSize(align = Alignment.CenterStart)
                                    .fillMaxWidth()
                                    .unboundClickable {
                                        navHostController.navigate(Router.AboutRouterEntity)
                                    }
                                    .padding(
                                        top = 10.dp,
                                        bottom = 10.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_about),
                                    fontSize = 15.sp
                                )
                                DspText(text = stringResource(id = R.string.setting_about_dsp))
                            }
                        }
                    }

                    //top bar
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 2 * alpha)
                            )
                            .fillMaxWidth()
                            .padding(
                                top = paddingValues.calculateTopPadding() + 10.dp,
                                bottom = 20.dp,
                                start = 10.dp
                            )
                            .onSizeChanged {
                                if (topBarHeight.intValue != it.height) {
                                    topBarHeight.intValue = it.height
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .unboundClickable {
                                    navHostController.popBackStack()
                                }
                        )

                        Text(
                            text = stringResource(id = R.string.setting_title),
                            fontSize = 20.sp,
                            modifier = Modifier.alpha(2 * alpha)
                        )
                    }
                }
            }
        )
    }

    val interfaceLanguages = mutableListOf(
        stringResource(id = R.string.follow_system) to LanguageManager.InterfaceLanguage.FOLLOW_SYSTEM,
        "中文"        to      LanguageManager.InterfaceLanguage.CHINESE,
        "English"    to      LanguageManager.InterfaceLanguage.ENGLISH
    )
    SingleSelectionDialog(
        data = interfaceLanguages,
        title = stringResource(id = R.string.setting_interface_language),
        icon = {
            Icon(
                imageVector = Icons.Outlined.Language,
                modifier = Modifier.size(22.dp),
                contentDescription = null
            )
        },
        selected = LanguageManager.InterfaceLanguage.entries.find { it.value == DataStore.interfaceLanguage },
        showDialog = showInterfaceLanguageDialog.value,
        onDismissRequest = {
            showInterfaceLanguageDialog.value = false
        },
        onSelect = {
            if (it != null) {
                interfaceLanguage.value = it.value
                DataStore.interfaceLanguage = it.value
            }
            LanguageManager.updateInterfaceLanguage(it, context)
        }
    )

    val dayNightModeData = mutableListOf(
        stringResource(id = R.string.follow_system)     to    ThemeManager.DayNightMode.FOLLOW_SYSTEM,
        stringResource(id = R.string.on)        to    ThemeManager.DayNightMode.NIGHT,
        stringResource(id = R.string.off)        to    ThemeManager.DayNightMode.DAY
    )
    SingleSelectionDialog(
        data = dayNightModeData,
        title = stringResource(id = R.string.setting_dark_mode),
        icon = {
            Icon(
                imageVector = Icons.Outlined.DarkMode,
                modifier = Modifier.size(22.dp),
                contentDescription = null
            )
        },
        selected = ThemeManager.DayNightMode.entries.find { it.value == DataStore.dayNightMode },
        showDialog = showDayNightModeDialog.value,
        onDismissRequest = { showDayNightModeDialog.value = false },
        onSelect = {
            if (it != null) {
                ThemeManager.toggleDayNightMode(it)
            }
        }
    )

    ColorPickerDialog(
        showDialog = showColorPickerDialog.value,
        initColor = if (customTheme.value.isEmpty()) null else Color(customTheme.value.toColorInt()),
        title = stringResource(id = R.string.setting_custom_theme),
        onDismissRequest = {
            showColorPickerDialog.value = false
        },
        onColorSelect = { seedColor, scheme ->
            customTheme.value = seedColor
            ThemeManager.toggleCustomTheme(seedColor)
            ThemeManager.toggleCustomThemeScheme(scheme)
            ThemeManager.toggleUseDynamicColor(false)
        }
    )
}

@Composable
fun DspText(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        modifier = Modifier.alpha(0.6f)
    )
}