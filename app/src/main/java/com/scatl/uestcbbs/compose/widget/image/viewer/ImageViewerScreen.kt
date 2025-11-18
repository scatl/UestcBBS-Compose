package com.scatl.uestcbbs.compose.widget.image.viewer

import android.Manifest
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.hasPermission
import com.scatl.uestcbbs.compose.ext.isGTESdk29
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.theme.DarkTheme
import com.scatl.uestcbbs.compose.widget.LoadingDialog
import kotlinx.coroutines.flow.collectLatest
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState
import java.io.File

/**
 * Created by sca_tl at 2024/8/26 15:28:53
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewerScreen(
    configId: String
) {
    val config = remember(configId) {
        ImageViewerConfig.getConfig(configId) ?: ImageViewerConfig()
    }

    val moreOptionsBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }
    val showSavingDialog = remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = config.initialIndex,
        pageCount = { config.images.size },
    )

    DarkTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Black
                )
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 1,
            ) { pageNum ->
                ImageItem(
                    modifier = Modifier.fillMaxSize(),
                    model = config.images[pageNum],
                    isActivePage = pagerState.settledPage == pageNum,
                    onLongClick = {
                        if (File(config.images[pageNum].originUrl ?: "").exists().not()) {
                            openBottomSheet.value = true
                        }
                    }
                )
            }

            TopBar(
                modifier = Modifier.align(Alignment.TopCenter),
                pagerState = pagerState,
                config = config
            )

            BottomBar(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = WindowInsets.navigationBars.getBottom(LocalDensity.current).px2dp),
                config = config,
                pagerState = pagerState,
                showSavingDialog = showSavingDialog
            )

            MoreOptions(
                config = config,
                sheetState = moreOptionsBottomSheetState,
                openSheet = openBottomSheet,
                showSavingDialog = showSavingDialog,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            LoadingDialog(
                showDialog = showSavingDialog.value,
                cancelable = false,
                text = stringResource(R.string.save_image_save_ing),
                onDismissRequest = {
                    showSavingDialog.value = false
                }
            )
        }
    }
}

@Composable
private fun ImageItem(
    model: ImageViewerConfig.ImageViewerItem,
    isActivePage: Boolean,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit
) {
    val navHostController = LocalNavController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val zoomableState = rememberZoomableState(
            zoomSpec = ZoomSpec(maxZoomFactor = 3f)
        )
        val focusRequester = remember { FocusRequester() }
        val imageState = rememberZoomableImageState(zoomableState)

        ZoomableAsyncImage(
            modifier = modifier.focusRequester(focusRequester),
            state = imageState,
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.originUrl)
                .placeholderMemoryCacheKey(model.thumbUrl)
                .crossfade(300)
                .build(),
            contentDescription = null,
            onClick = {
                navHostController.popBackStack()
            },
            onLongClick = {
                onLongClick.invoke()
            }
        )

        // Focus the image so that it can receive keyboard and mouse shortcut events.
        if (isActivePage) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = !imageState.isImageDisplayed
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        if (!isActivePage) {
            LaunchedEffect(Unit) {
                zoomableState.resetZoom()
            }
        }
    }
}

@Composable
private fun TopBar(
    config: ImageViewerConfig,
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    val navHostController = LocalNavController.current

    Box (
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 15.dp)
                .clickable(unbound = true) {
                    navHostController.popBackStack()
                }
        )

        Text(
            text = "${pagerState.currentPage + 1} / ${config.images.size}",
            fontSize = 13.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(vertical = 2.dp, horizontal = 15.dp)
        )
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    config: ImageViewerConfig,
    pagerState: PagerState,
    showSavingDialog: MutableState<Boolean>
) {

    val context = LocalContext.current
    val showSaveBtn = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collectLatest { currentPage ->
            showSaveBtn.value = File(config.images[currentPage].originUrl ?: "").exists().not()
        }
    }

    fun downloadCurrentImg() {
        showSavingDialog.value = false

        val urls = mutableListOf<String>()
        config.images.getOrNull(pagerState.currentPage)?.originUrl?.let {
            urls.add(it)
        }

        val intent = Intent(context, ImageSaveService::class.java).apply {
            putExtra("urls", urls.toTypedArray())
        }
        context.startService(intent)

//        ImageSaveUtil.saveImages(context, urls) {
//            showSavingDialog.value = false
//            if (it == urls.size) {
//                ContextCompat.getString(context, R.string.save_image_successful)
//            } else {
//                ContextCompat.getString(context, R.string.save_image_fail)
//            }.showToast(context)
//        }
    }

    var hasWritePermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasWritePermission = isGranted
        if (hasWritePermission) {
            downloadCurrentImg()
        }
    }

    LaunchedEffect(context) {
        hasWritePermission = context.hasPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    if (showSaveBtn.value) {
        Text(
            text = stringResource(id = R.string.save),
            color = Color.White,
            fontSize = 14.sp,
            modifier = modifier
                .padding(30.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50)
                )
                .padding(vertical = 5.dp)
                .padding(start = 20.dp, end = 20.dp)
                .unboundClickable {
                    if (isGTESdk29() || hasWritePermission) {
                        downloadCurrentImg()
                    } else {
                        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptions(
    sheetState: SheetState,
    openSheet: MutableState<Boolean>,
    modifier: Modifier,
    config: ImageViewerConfig,
    showSavingDialog: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun downloadAllImages() {
        val urls = mutableListOf<String>()
        config.images.forEach {
            it.originUrl?.let {
                urls.add(it)
            }
        }
        val intent = Intent(context, ImageSaveService::class.java).apply {
            putExtra("urls", urls.toTypedArray())
        }
        context.startService(intent)
//        ImageSaveUtil.saveImages(context, urls) {
//            showSavingDialog.value = false
//            if (it == urls.size) {
//                ContextCompat.getString(context, R.string.save_all_images_successful)
//            } else {
//                ContextCompat.getContextForLanguage(context).getString(R.string.save_images_successful_part, it.toString(), (urls.size - it).toString())
//            }.showToast(context)
//        }
    }

    var hasWritePermission by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        hasWritePermission = isGranted
        if (hasWritePermission) {
            downloadAllImages()
        }
    }

    LaunchedEffect(context) {
        hasWritePermission = context.hasPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    BackHandler(enabled = openSheet.value) {
        scope.launchSafety {
            sheetState.hide()
        }
    }

    if (openSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openSheet.value = false
            },
            sheetState = sheetState,
            //modifier = modifier.height(150.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(80.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp)
                    //.fillMaxHeight(0.13f)
                ,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                state = rememberLazyGridState(),
                userScrollEnabled = false
            ) {
                item {
                    MoreOptionItem(
                        text = "保存全部",
                        icon = Icons.Outlined.Save
                    ) {
                        openSheet.value = false
                        showSavingDialog.value = false
                        if (isGTESdk29() || hasWritePermission) {
                            downloadAllImages()
                        } else {
                            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreOptionItem(
    text: String,
    icon: ImageVector,
    enable: Boolean = true,
    onClick: () -> Unit
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.alpha(if (enable) 1f else 0.5f)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                )
                .unboundClickable {
                    if (enable) {
                        onClick.invoke()
                    }
                }
                .padding(15.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
