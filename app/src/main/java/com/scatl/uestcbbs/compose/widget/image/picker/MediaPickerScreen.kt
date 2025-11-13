package com.scatl.uestcbbs.compose.widget.image.picker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.hasPermission
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.rememberMutableStateListOf
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.module.video.VideoPlayerActivity
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.RoundCheckBox
import com.scatl.uestcbbs.compose.widget.RoundCheckBoxDefaults
import com.scatl.uestcbbs.compose.widget.image.viewer.ImageViewerConfig
import com.scatl.uestcbbs.compose.widget.refresh.EmptyContent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by sca_tl at 2024/10/28 18:40:03
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun MediaPickerScreen(
    config: MediaPickerConfig = MediaPickerConfig()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hazeState = rememberHazeState()
    val navHostController = LocalNavController.current
    val showErrorView = rememberSaveable { mutableStateOf(false) }
    val galleryEntity = rememberSaveable { mutableStateOf(GalleryEntity()) }
    val currentAlbum = rememberSaveable { mutableStateOf(AlbumEntity()) }
    val showAlbumSelect = rememberSaveable { mutableStateOf(false) }
    val currentSelect = rememberMutableStateListOf(config.initMedia)

    fun loadMedia() {
        scope.launchSafety {
            galleryEntity.value = withContext(Dispatchers.IO) {
                val result = MediaStoreUtil.queryImages(context)
                currentAlbum.value = result.albums[0]
                result
            }
        }
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val allGranted = result.all { it.value }
            if (allGranted) {
                loadMedia()
                showErrorView.value = false
            } else {
                showErrorView.value = true
            }
        }

    LaunchedEffect(currentSelect.size) {
        withContext(Dispatchers.IO) {
            galleryEntity.value.albums.forEach { albumEntity ->
                albumEntity.selectedMedia.clear()
                currentSelect.forEach {
                    if (albumEntity.allMedia.contains(it) && albumEntity.selectedMedia.contains(it).not()) {
                        albumEntity.selectedMedia.add(it)
                    }
                }
            }
        }
    }

    BackHandler (showAlbumSelect.value) {
        showAlbumSelect.value = false
    }

    LoadInitialDataIfNeeded(context) {
        val granted = context.hasPermission(permissions)
        if (granted) {
            loadMedia()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .hazeEffect(state = hazeState, style = HazeMaterials.regular(MaterialTheme.colorScheme.surface)),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                title = {
                    Text(
                        text = "选择媒体"
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(30.dp)
                            .clickable(unbound = true) {
                                navHostController.popBackStack()
                            }
                    )
                }
            )
        },
    ) { padding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (showErrorView.value) {
                EmptyView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    requestPermissionLauncher = requestPermissionLauncher
                )
            } else {
                ImageGrid(
                    currentAlbum = currentAlbum,
                    showAlbumSelect = showAlbumSelect,
                    currentSelect = currentSelect,
                    hazeState = hazeState,
                    padding = padding,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = BottomSheetDefaults.ScrimColor.copy(
                                alpha = animateFloatAsState(
                                    targetValue = if (showAlbumSelect.value) 0.4f else 0f,
                                    animationSpec = tween(durationMillis = 500),
                                    label = "bg_alpha"
                                ).value
                            )
                        )
                        .padding(padding)
                        .clickable(radius = 0.dp, enable = showAlbumSelect.value) {
                            showAlbumSelect.value = showAlbumSelect.value.not()
                        }
                )

                BottomBar(
                    currentAlbum = currentAlbum,
                    galleryEntity = galleryEntity,
                    showAlbumSelect = showAlbumSelect,
                    currentSelect = currentSelect,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer
                        )
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun EmptyView(
    modifier: Modifier,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
) {
    val context = LocalContext.current

    EmptyContent(
        modifier = modifier,
        error = true,
        errorData = Throwable("由于您拒绝了相册权限，无法显示相册"),
        onClick = {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permissions[0])) {
                requestPermissionLauncher.launch(permissions)
            } else {
                "请手动设置权限".showToast(context)
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.also { intent ->
                    context.startActivity(intent)
                }
            }
        }
    )
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
private fun ImageGrid(
    currentAlbum: MutableState<AlbumEntity>,
    showAlbumSelect: MutableState<Boolean>,
    currentSelect: SnapshotStateList<MediaEntity>,
    hazeState: HazeState,
    padding: PaddingValues,
    modifier: Modifier
) {
    val navHostController = LocalNavController.current
    val context = LocalContext.current

    fun toImageViewer(mediaEntity: MediaEntity) {
        val imageViewerItems = mutableListOf<ImageViewerConfig.ImageViewerItem>()
        currentAlbum.value.allMedia.forEach {
            if (it.isVideo.not()) {
                imageViewerItems.add(
                    ImageViewerConfig.ImageViewerItem(
                        originUrl = it.absolutePath,
                        thumbUrl = it.absolutePath
                    )
                )
            }
        }

        navHostController.navigate(
            Router.ImageViewerRouterEntity(
                config = ImageViewerConfig.toJson(
                    ImageViewerConfig(
                        images = imageViewerItems,
                        initialIndex = imageViewerItems.indexOf(imageViewerItems.find { it.originUrl == mediaEntity.absolutePath })
                    )
                )
            )
        )
    }

    LazyVerticalGrid(
        modifier = modifier
            .hazeSource(hazeState)
            .offset(
                x = 0.dp,
                y = animateDpAsState(
                    targetValue = if (showAlbumSelect.value) (-50).dp else 0.dp,
                    animationSpec = tween(durationMillis = 500),
                    label = "offset"
                ).value
            ),
        contentPadding = padding,
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        currentAlbum.value.allMedia.forEachIndexed { _, mediaEntity ->
            item {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .animateItem()
                        .clickable(unbound = false) {
                            if (mediaEntity.isVideo) {
                                VideoPlayerActivity.open(context, mediaEntity.absolutePath.toString(), mediaEntity.name)
                            } else {
                                toImageViewer(mediaEntity)
                            }
                        }
                ) {
                    AsyncImage(
                        model = mediaEntity.absolutePath,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(
                                shape = RoundedCornerShape(0.dp)
                            )
                            .scale(
                                animateFloatAsState(
                                    animationSpec = tween(500),
                                    targetValue = if (currentSelect.contains(mediaEntity)) {
                                        1.2f
                                    } else {
                                        1f
                                    },
                                    label = "img_scale"
                                ).value
                            )
                    )

                    if (mediaEntity.isHeic || mediaEntity.isWebp || mediaEntity.isGif || mediaEntity.isVideo) {
                        Text(
                            text = if (mediaEntity.isHeic) {
                                "HEIC"
                            } else if (mediaEntity.isWebp) {
                                "WEBP"
                            } else if (mediaEntity.isGif) {
                                "GIF"
                            } else {
                                "VIDEO"
                            },
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(
                                        topEnd = 5.dp
                                    )
                                )
                                .padding(
                                    horizontal = 5.dp,
                                    vertical = 2.dp
                                )
                                .align(Alignment.BottomStart)
                        )

                        if (mediaEntity.isVideo) {
                            Icon(
                                imageVector = Icons.Outlined.PlayCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                color = Color.Black.copy(
                                    animateFloatAsState(
                                        animationSpec = tween(500),
                                        targetValue = if (currentSelect.contains(mediaEntity)) {
                                            0.5f
                                        } else {
                                            0f
                                        },
                                        label = "select_shadow"
                                    ).value
                                )
                            )
                    )

                    RoundCheckBox(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(5.dp),
                        isChecked = currentSelect.contains(mediaEntity),
                        borderWidth = 2.dp,
                        radius = 9.dp,
                        onClick = {
                            if (currentSelect.contains(mediaEntity)) {
                                currentSelect.remove(mediaEntity)
                            } else {
                                currentSelect.add(mediaEntity)
                            }
                        },
                        color = RoundCheckBoxDefaults.colors(
                            borderColor = Color.White,
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BottomBar(
    modifier: Modifier,
    galleryEntity: MutableState<GalleryEntity>,
    currentAlbum: MutableState<AlbumEntity>,
    showAlbumSelect: MutableState<Boolean>,
    currentSelect: SnapshotStateList<MediaEntity>,
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(unbound = false) {
                    showAlbumSelect.value = showAlbumSelect.value.not()
                }
                .height(60.dp)
                .padding(horizontal = 20.dp)
        ) {
            Row (
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardDoubleArrowDown,
                    contentDescription = null
                )
                Text(
                    text = "${currentAlbum.value.albumName}(${currentAlbum.value.allMedia.size}个媒体)",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LookaheadScope {
                Text(
                    text = if (currentSelect.isEmpty()) {
                        "确认"
                    } else {
                        "确认(${currentSelect.size})"
                    },
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = animateFloatAsState(
                                    targetValue = if (currentSelect.isEmpty()) 0.4f else 1f,
                                    label = "btn_alpha"
                                ).value
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(unbound = false) {
                            if (currentSelect.isNotEmpty()) {

                            }
                        }
                        //.animateContentSize()
                        .animateBounds(
                            lookaheadScope = this@LookaheadScope
                        )
                        .padding(
                            vertical = 5.dp,
                            horizontal = 20.dp
                        )
                )
            }

        }

        AnimatedVisibility(
            visible = showAlbumSelect.value
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
            ) {
                galleryEntity.value.albums.forEachIndexed { _, albumEntity ->
                    item {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(unbound = false) {
                                    showAlbumSelect.value = false
                                    currentAlbum.value = albumEntity
                                }
                                .padding(vertical = 10.dp, horizontal = 20.dp)
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                AsyncImage(
                                    model = albumEntity.coverImage,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(
                                            shape = RoundedCornerShape(20)
                                        )
                                )
                                Column {
                                    Text(
                                        text = albumEntity.albumName.toString(),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "(已选${albumEntity.selectedMedia.size}个)${albumEntity.allMedia.size}个媒体",
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            RadioButton(
                                selected = albumEntity.albumPath == currentAlbum.value.albumPath,
                                onClick = {
                                    showAlbumSelect.value = false
                                    currentAlbum.value = albumEntity
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

val permissions =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }