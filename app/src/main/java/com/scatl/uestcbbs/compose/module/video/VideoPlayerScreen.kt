package com.scatl.uestcbbs.compose.module.video

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Parcelable
import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.getStatusBarHeight
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.theme.DarkTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

/**
 * Created by sca_tl at 2025/2/19 10:49:43
 */
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    videoName: String?,
    playerViewModel: VideoPlayerVideoModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    val navHostController = LocalNavController.current
    val configuration = LocalConfiguration.current
    val player by playerViewModel.playerState.collectAsState()
    val currentPosition = rememberSaveable { mutableStateOf(player?.currentPosition) }
    val isFullscreen = rememberSaveable { mutableStateOf(false) }
    val isPlaying = rememberSaveable { mutableStateOf<Boolean?>(false) }
    val isControlShow = rememberSaveable { mutableStateOf(false) }
    val currentSpeed = rememberSaveable { mutableStateOf(Speed.Speeds.SPEED_1_0.speed) }
    val isLongPress = rememberSaveable { mutableStateOf(false) }
    val isPortraitInit = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (isFullscreen.value && isPortraitInit.value) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        } else {
            navHostController.popBackStack()
        }
    }

    LoadInitialDataIfNeeded(context) {
        isPortraitInit.value = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    LaunchedEffect(videoUrl) {
        playerViewModel.initializePlayer(context, videoUrl)
    }

    LaunchedEffect(configuration) {
        isFullscreen.value = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    DisposableEffect(Unit) {
        onDispose {
            playerViewModel.savePlayerState()
            playerViewModel.releasePlayer()
        }
    }

    LaunchedEffect(player) {
        isPlaying.value = player?.playWhenReady
        while (true) {
            withFrameNanos {
                currentPosition.value = player?.currentPosition
            }
        }
    }

    DarkTheme {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter
            ) {
                VideoPlayer(
                    player = player,
                    isControlShow = isControlShow,
                    currentSpeed = currentSpeed,
                    isPlaying = isPlaying,
                    isLongPress = isLongPress
                )

                BottomControls(
                    player = player,
                    currentPosition = currentPosition,
                    isFullscreen = isFullscreen,
                    isPlaying = isPlaying,
                    isControlShow = isControlShow,
                    currentSpeed = currentSpeed
                )

                if (isLongPress.value) {
                    FastForwardIndicator(
                        currentSpeed = currentSpeed,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                    )
                }
            }

            MiddleControls(
                player = player,
                currentPosition = currentPosition,
                isFullscreen = isFullscreen,
                isPlaying = isPlaying,
                isControlShow = isControlShow,
                currentSpeed = currentSpeed
            )

            AnimatedVisibility(
                visible = (isFullscreen.value && isControlShow.value) || isFullscreen.value.not(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier

                        .padding(top = getStatusBarHeight(context) + 10.dp, start = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(unbound = true) {
                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                                navHostController.popBackStack()
                            }
                    )
                    if (videoName.isNotNullAndEmpty()) {
                        Text(
                            text = videoName,
                            fontSize = 16.sp,
                            color = Color.White,
                            maxLines = 1,
                            modifier = Modifier
                                .basicMarquee()
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun MiddleControls(
    modifier: Modifier = Modifier,
    player: ExoPlayer?,
    currentPosition: MutableState<Long?>,
    isFullscreen: MutableState<Boolean>,
    isPlaying: MutableState<Boolean?>,
    isControlShow: MutableState<Boolean>,
    currentSpeed: MutableState<Speed>
) {
    AnimatedVisibility(
        visible = isControlShow.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(0.2f))
                .padding(vertical = 10.dp, horizontal = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Replay10,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(unbound = true) {
                        player?.seekTo(maxOf(player.currentPosition - 10000, 0))
                    }
            )

            Icon(
                imageVector = if (isPlaying.value == true) Icons.Outlined.PauseCircle else Icons.Outlined.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(45.dp)
                    .clickable(unbound = true) {
                        playOrPause(player, isPlaying)
                    }
            )

            Icon(
                imageVector = Icons.Outlined.Forward10,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(unbound = true) {
                        player?.seekTo(minOf(player.currentPosition + 10000, player.duration))
                    }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun VideoPlayer(
    player: ExoPlayer?,
    isControlShow: MutableState<Boolean>,
    currentSpeed: MutableState<Speed>,
    isPlaying: MutableState<Boolean?>,
    isLongPress: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()
    var originSpeed = currentSpeed.value.copy()
    val isTouching = rememberSaveable { mutableStateOf<Boolean>(false) }

    LaunchedEffect(isTouching.value) {
        if (isControlShow.value && isTouching.value.not()) {
            scope.launchSafety {
                delay(3000)
                if (isTouching.value.not()) {
                    isControlShow.value = false
                }
            }
        }
    }

    AndroidView(
        modifier = Modifier
//            .pointerInteropFilter { event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        isTouching = true
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        isTouching = false
//                    }
//                }
//                false
//            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        if (offset.x > size.width / 2 && isControlShow.value.not()) {
                            isLongPress.value = true
                            originSpeed = currentSpeed.value.copy()
                            player?.setPlaybackSpeed(Speed.getFastSpeed(currentSpeed.value).speed.speed)
                        }
                    },
                    onPress = {
                        tryAwaitRelease()
                        if (isLongPress.value) {
                            isLongPress.value = false
                            currentSpeed.value = originSpeed
                            player?.setPlaybackSpeed(currentSpeed.value.speed)
                        }
                    },
                    onTap = {
                        isControlShow.value = isControlShow.value.not()
                    },
                    onDoubleTap = {
                        playOrPause(player, isPlaying)
                    }
                )
            },
        factory = { context ->
            PlayerView(context).apply {
                this.player = player
                this.useController = false
            }
        },
        update = { playerView ->
            playerView.player = player
        }
    )
}

private fun playOrPause(
    player: ExoPlayer?,
    isPlaying: MutableState<Boolean?>,
) {
    if (isPlaying.value == true) {
        player?.pause()
    } else {
        player?.play()
    }
    isPlaying.value = isPlaying.value?.not()
}

@Composable
private fun BottomControls(
    modifier: Modifier = Modifier,
    player: ExoPlayer?,
    currentPosition: MutableState<Long?>,
    isFullscreen: MutableState<Boolean>,
    isPlaying: MutableState<Boolean?>,
    isControlShow: MutableState<Boolean>,
    currentSpeed: MutableState<Speed>
) {
    val activity = LocalActivity.current
    val showSpeedSelect = rememberSaveable { mutableStateOf(false) }
    AnimatedVisibility(
        visible = isControlShow.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                    )
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = if (isFullscreen.value) 20.dp else 5.dp
                )
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = formatTime(currentPosition.value ?: 0L) + " / " + formatTime(player?.duration ?: 0L),
                    color = Color.White,
                    fontSize = 12.sp
                )

                Slider(
                    value = (currentPosition.value?.toFloat() ?: 0F) / (player?.duration?.toFloat() ?: 1F),
                    trackColor = Color.White,
                    onValueChange = {
                        player?.seekTo((player.duration * it).toLong())
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp)
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box {
                        Text(
                            text = currentSpeed.value.dsp,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable(unbound = true) {
                                    showSpeedSelect.value = true
                                }
                        )

                        DropdownMenu(
                            expanded = showSpeedSelect.value,
                            onDismissRequest = { showSpeedSelect.value = false },
                        ) {
                            Speed.Speeds.entries.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = it.speed.dsp
                                        )
                                    },
                                    trailingIcon = {
                                        if (currentSpeed.value == it.speed) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    onClick = {
                                        showSpeedSelect.value = false
                                        player?.setPlaybackSpeed(it.speed.speed)
                                        currentSpeed.value = it.speed
                                    }
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = if (isFullscreen.value) Icons.Outlined.FullscreenExit else Icons.Outlined.Fullscreen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .clickable(unbound = true) {
                                if (isFullscreen.value) {
                                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                                } else {
                                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                }
                                isFullscreen.value = isFullscreen.value.not()
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun FastForwardIndicator(
    currentSpeed: MutableState<Speed>,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(0.35f))
            .padding(vertical = 10.dp, horizontal = 5.dp)
    ) {
        FastForwardAnimation()

        Text(
            text = "${Speed.getFastSpeed(currentSpeed.value).speed.speed}倍速播放中",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.offset((-12).dp)
        )
    }
}

@Composable
private fun FastForwardAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()

    Row(modifier) {
        repeat(3) { index ->
            val color by transition.animateColor(
                initialValue = Color.LightGray.copy(alpha = 0.1f),
                targetValue = Color.LightGray,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 250)
                )
            )

            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                modifier = Modifier.offset(-(index * 12).dp),
                tint = color
            )
        }
    }
}

@Composable
fun formatTime(timeMs: Long): String {
    val seconds = (timeMs / 1000) % 60
    val minutes = (timeMs / (1000 * 60) % 60)
    val hours = (timeMs / (1000 * 60 * 60))

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Parcelize
data class Speed(
    val speed: Float,
    val dsp: String
): Parcelable {
    enum class Speeds(val speed: Speed) {
        SPEED_0_5(Speed(0.5f, "0.5X"),),
        SPEED_1_0(Speed(1.0f, "1.0X")),
        SPEED_1_5(Speed(1.5f, "1.5X")),
        SPEED_2_0(Speed(2.0f, "2.0X")),
        SPEED_3_0(Speed(3.0f, "3.0X"))
    }

    companion object {
        val min : Speed
            get() = Speeds.entries.first().speed

        val max : Speed
            get() = Speeds.entries.last().speed

        fun getFastSpeed(speed: Speed): Speeds {
            return when(speed.speed) {
                Speeds.SPEED_0_5.speed.speed -> Speeds.SPEED_1_0
                Speeds.SPEED_1_0.speed.speed -> Speeds.SPEED_2_0
                Speeds.SPEED_1_5.speed.speed -> Speeds.SPEED_3_0
                Speeds.SPEED_2_0.speed.speed -> Speeds.SPEED_3_0
                Speeds.SPEED_3_0.speed.speed -> Speeds.SPEED_3_0
                else -> Speeds.SPEED_1_0
            }
        }
    }
}