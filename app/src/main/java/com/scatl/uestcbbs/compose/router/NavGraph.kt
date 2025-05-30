package com.scatl.uestcbbs.compose.router

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.auth.AccountManageScreen
import com.scatl.uestcbbs.compose.module.auth.AddAccountScreen
import com.scatl.uestcbbs.compose.module.collection.CollectionDetailScreen
import com.scatl.uestcbbs.compose.module.collection.CollectionScreen
import com.scatl.uestcbbs.compose.module.dayquestion.DayQuestionScreen
import com.scatl.uestcbbs.compose.module.download.DownloadScreen
import com.scatl.uestcbbs.compose.module.forum.detail.ForumDetailScreen
import com.scatl.uestcbbs.compose.module.history.BrowsingHistoryScreen
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerScreen
import com.scatl.uestcbbs.compose.module.magic.MagicShopScreen
import com.scatl.uestcbbs.compose.module.main.MainScreen
import com.scatl.uestcbbs.compose.module.medal.MedalCenterScreen
import com.scatl.uestcbbs.compose.module.message.chat.ChatDetailScreen
import com.scatl.uestcbbs.compose.module.post.CreateThreadScreen
import com.scatl.uestcbbs.compose.module.post.ThreadDetailScreen
import com.scatl.uestcbbs.compose.module.post.commentrate.CommentRateScreen
import com.scatl.uestcbbs.compose.module.search.SearchScreen
import com.scatl.uestcbbs.compose.module.setting.AboutScreen
import com.scatl.uestcbbs.compose.module.setting.OpenSourceScreen
import com.scatl.uestcbbs.compose.module.setting.SettingScreen
import com.scatl.uestcbbs.compose.module.snapshot.SnapShotScreen
import com.scatl.uestcbbs.compose.module.user.UserProfileScreen
import com.scatl.uestcbbs.compose.module.video.VideoPlayerScreen
import com.scatl.uestcbbs.compose.module.watertask.WaterTaskScreen
import com.scatl.uestcbbs.compose.util.BBSLinkUtil
import com.scatl.uestcbbs.compose.util.LinkType
import com.scatl.uestcbbs.compose.widget.image.picker.MediaPickerConfig
import com.scatl.uestcbbs.compose.widget.image.picker.MediaPickerScreen

/**
 * Created by sca_tl at 2024/4/23 10:31:21
 */

val LocalNavController = compositionLocalOf<NavHostController> {
    error("NavController not provided")
}

@Composable
fun NavGraph(
    dataBase: AppDataBase
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val startDestination = remember {
        if (dataBase.getAccountDao().getAllAccounts().isEmpty()) {
            Router.AddAccountRouterEntity
        } else if (dataBase.getAccountDao().getSignedInAccount() == null) {
            Router.AccountManageRouterEntity
        } else {
            Router.MainRouterEntity
        }
    }

    CompositionLocalProvider(LocalNavController provides navController) {
        ModalBottomSheetLayout (
            bottomSheetNavigator = bottomSheetNavigator,
            sheetBackgroundColor = Color.Transparent,
            sheetElevation = 0.dp
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { scaleIntoContainer() },
                exitTransition = { scaleOutOfContainer(direction = ScaleTransitionDirection.INWARDS) },
                popEnterTransition = { scaleIntoContainer(direction = ScaleTransitionDirection.OUTWARDS) },
                popExitTransition = { scaleOutOfContainer() }
            ) {
                composable<Router.MainRouterEntity> {
                    MainScreen()
                }

                composable<Router.AddAccountRouterEntity> {
                    AddAccountScreen()
                }

                composable<Router.AccountManageRouterEntity> {
                    AccountManageScreen()
                }

                composable<Router.SettingRouterEntity> {
                    SettingScreen()
                }

                composable<Router.AboutRouterEntity> {
                    AboutScreen()
                }

                composable<Router.OpenSourceRouterEntity> {
                    OpenSourceScreen()
                }

                composable<Router.ThreadDetailRouterEntity> {
                    val entity: Router.ThreadDetailRouterEntity = it.toRoute()
                    ThreadDetailScreen(
                        routerEntity = entity
                    )
                }

                composable<Router.SnapshotRouterEntity> {
                    SnapShotScreen()
                }

                composable<Router.DayQuestionRouterEntity> {
                    DayQuestionScreen()
                }

                composable<Router.UserProfileRouterEntity> {
                    val entity: Router.UserProfileRouterEntity = it.toRoute()
                    UserProfileScreen(
                        routerEntity = entity
                    )
                }

                composable<Router.ForumDetailRouterEntity> {
                    val entity: Router.ForumDetailRouterEntity = it.toRoute()
                    ForumDetailScreen(
                        fid = entity.fid
                    )
                }

                composable<Router.ChatDetailRouterEntity> {
                    val entity: Router.ChatDetailRouterEntity = it.toRoute()
                    ChatDetailScreen(
                        routerEntity = entity
                    )
                }

                composable<Router.ImageViewerRouterEntity> {
                    val entity: Router.ImageViewerRouterEntity = it.toRoute()
                    val imageViewerConfig = ImageViewerConfig.fromJson(entity.config)
                    if (imageViewerConfig.images.isNotEmpty()) {
                        ImageViewerScreen(
                            config = imageViewerConfig
                        )
                    }
                }

                composable<Router.HistoryRouterEntity> {
                    BrowsingHistoryScreen()
                }

                composable<Router.SearchRouterEntity> {
                    SearchScreen()
                }

                composable<Router.MagicShopRouterEntity> {
                    MagicShopScreen()
                }

                composable<Router.MedalRouterEntity> {
                    MedalCenterScreen()
                }

                composable<Router.CollectionListRouterEntity> {
                    val entity: Router.CollectionListRouterEntity = it.toRoute()
                    CollectionScreen(
                        create = entity.create
                    )
                }

                composable<Router.CollectionDetailRouterEntity> {
                    val entity: Router.CollectionDetailRouterEntity = it.toRoute()
                    CollectionDetailScreen(
                        collectionId = entity.id
                    )
                }

                composable<Router.MediaPickerRouterEntity> {
                    val entity: Router.MediaPickerRouterEntity = it.toRoute()
                    MediaPickerScreen(
                        config = MediaPickerConfig.fromJson(entity.config)
                    )
                }

                composable<Router.CreateThreadRouterEntity> {
                    CreateThreadScreen()
                }

                composable<Router.VideoPlayerRouterEntity> {
                    val entity: Router.VideoPlayerRouterEntity = it.toRoute()
                    VideoPlayerScreen(
                        videoUrl = entity.url,
                        videoName = entity.name
                    )
                }

                bottomSheet<Router.PostCommentAndRateRouterEntity> {
                    val entity: Router.PostCommentAndRateRouterEntity = it.toRoute()
                    CommentRateScreen(
                        routerEntity = entity
                    )
                }

                bottomSheet<Router.DownloadRouterEntity>{
                    val entity: Router.DownloadRouterEntity = it.toRoute()
                    if (entity.url.isNotNullAndEmpty()) {
                        DownloadScreen(
                            routerEntity = entity
                        )
                    }
                }

                bottomSheet<Router.WaterTaskRouterEntity> {
                    WaterTaskScreen()
                }
            }
        }
    }
}

fun linkNavigate(
    url: String?,
    openBrowserIfNotMatch: Boolean = true,
    navHostController: NavHostController,
    uriHandler: UriHandler,
): Boolean {
    if (url.isNullOrEmpty()) {
        return false
    }

    val linkType = BBSLinkUtil.getLinkType(url)

    if (linkType != LinkType.Unknown) {
        when (linkType) {
            is LinkType.ThreadDetail -> {
                navHostController.navigate(
                    Router.ThreadDetailRouterEntity(
                        id = linkType.id.toIntOrElse(),
                        pid = linkType.pid
                    )
                )
            }
            is LinkType.UserDetail -> {
                navHostController.navigate(
                    Router.UserProfileRouterEntity(
                        uid = linkType.id,
                        name = linkType.name
                    )
                )
            }
            is LinkType.ForumDetail -> {
                navHostController.navigate(
                    Router.ForumDetailRouterEntity(
                        fid = linkType.id.toIntOrElse()
                    )
                )
            }
            is LinkType.Task -> {
                navHostController.navigate(Router.WaterTaskRouterEntity)
            }
            is LinkType.Magic -> {
                navHostController.navigate(Router.MagicShopRouterEntity)
            }
            is LinkType.Medal -> {
                navHostController.navigate(Router.MedalRouterEntity)
            }
            is LinkType.Collection -> {
                if (linkType.id > 0) {
                    navHostController.navigate(
                        Router.CollectionDetailRouterEntity(
                            id = linkType.id
                        )
                    )
                } else {
                    navHostController.navigate(
                        Router.CollectionListRouterEntity()
                    )
                }
            }
            else -> {

            }
        }
        return true
    }
    if (openBrowserIfNotMatch) {
        uriHandler.openUri(url)
        return true
    }
    return false
}

fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 0.9f else 1.1f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(durationMillis = 250, delayMillis = 100),
        initialScale = initialScale
    ) + fadeIn(
        animationSpec = tween(durationMillis = 250, delayMillis = 100)
    )
}

fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 0.9f else 1.1f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(durationMillis = 250, delayMillis = 100),
        targetScale = targetScale
    ) + fadeOut(
        animationSpec = tween(delayMillis = 100)
    )
}

enum class ScaleTransitionDirection {
    INWARDS,
    OUTWARDS
}