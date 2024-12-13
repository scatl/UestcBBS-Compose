package com.scatl.uestcbbs.compose.module.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.eventbus.BaseEvent
import com.scatl.uestcbbs.compose.eventbus.Event
import com.scatl.uestcbbs.compose.eventbus.SharedFlowBus
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.manager.MessageManager
import com.scatl.uestcbbs.compose.module.forum.ForumCategoryScreen
import com.scatl.uestcbbs.compose.module.home.HomeScreen
import com.scatl.uestcbbs.compose.module.message.MessageScreen
import com.scatl.uestcbbs.compose.module.user.mine.MineScreen
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.theme.LocalCustomColors

/**
 * Created by sca_tl at 2024/4/23 10:29:59
 */
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val currItem = rememberSaveable { mutableIntStateOf(0) }
    val bottomNavigationShow = rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    val saveableStateHolder = rememberSaveableStateHolder()

    DisposableEffect(context) {
        val observer = Observer<Any> {
            (it as BaseEvent.MainNavVisibleEvent).visible?.let { visible ->
                bottomNavigationShow.value = visible
            }
        }

        val liveData = SharedFlowBus.on(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY)
        liveData.observe(context as LifecycleOwner, observer)

        onDispose {
            liveData.removeObserver(observer)
        }
    }

    val bottomBarItems = mutableListOf(
        stringResource(id = R.string.bottom_bar_home) to Icons.Outlined.Home,
        stringResource(id = R.string.bottom_bar_forum) to Icons.Outlined.Dashboard,
        stringResource(id = R.string.bottom_bar_message) to Icons.Outlined.Forum,
        stringResource(id = R.string.bottom_bar_mine) to Icons.Outlined.PersonOutline
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = bottomNavigationShow.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .animateContentSize()
                ) {
                    bottomBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            label = { Text(item.first) },
                            icon = {
                                Icon(index, item)
                            },
                            selected = currItem.intValue == index,
                            onClick = {
                                currItem.intValue = index
                            },
                        )
                    }
                }
            }
        }
    ) {
        Column {
            when (currItem.intValue) {
                0 -> saveableStateHolder.SaveableStateProvider(
                    key = bottomBarItems[currItem.intValue].first,
                    content = {
                        HomeScreen()
                    }
                )

                1 -> saveableStateHolder.SaveableStateProvider(
                    key = bottomBarItems[currItem.intValue].first,
                    content = {
                        ForumCategoryScreen()
                    }
                )

                2 -> saveableStateHolder.SaveableStateProvider(
                    key = bottomBarItems[currItem.intValue].first,
                    content = {
                        MessageScreen()
                    }
                )

                3 -> saveableStateHolder.SaveableStateProvider(
                    key = bottomBarItems[currItem.intValue].first,
                    content = {
                        MineScreen()
                    }
                )
            }
        }
    }
}

@Composable
private fun Icon(
    index: Int,
    item:  Pair<String, ImageVector>
) {
    val unreadEntity by MessageManager.unreadCount.collectAsState()

    if (index == 2) {
        BadgedBox(
            badge = {
                if (unreadEntity.totalUnreadCount > 0) {
                    Badge (
                        modifier = Modifier
                            .offset(x = 2.dp, y = (-2).dp)
                    ) {
                        Text(
                            text = unreadEntity.totalUnreadCount.toString(),
                            color = LocalCustomColors.current.unreadBadgeText
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = item.second,
                contentDescription = item.first
            )
        }
    } else {
        Icon(
            imageVector = item.second,
            contentDescription = item.first
        )
    }
}
