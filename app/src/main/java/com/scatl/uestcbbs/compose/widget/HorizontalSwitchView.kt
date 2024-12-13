package com.scatl.uestcbbs.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

/**
 * Created by sca_tl at 2024/9/26 17:10:02
 */
@Composable
fun HorizontalSwitchView (
    showContent2: MutableState<Boolean>,
    content1: @Composable () -> Unit,
    content2: @Composable () -> Unit,
    animDuration: Int = 500
) {
    AnimatedVisibility (
        visible = !showContent2.value,
        enter = slideInHorizontally(
            initialOffsetX = { if (showContent2.value) it else -it },
            animationSpec = tween(durationMillis = animDuration)
        ) + fadeIn(
            animationSpec = tween(durationMillis = animDuration)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { if (showContent2.value) -it else it },
            animationSpec = tween(durationMillis = animDuration)
        ) + fadeOut(
            animationSpec = tween(durationMillis = animDuration)
        )
    ) {
        content1()
    }

    AnimatedVisibility(
        visible = showContent2.value,
        enter = slideInHorizontally(
            initialOffsetX = { if (showContent2.value) it else -it },
            animationSpec = tween(durationMillis = animDuration)
        ) + fadeIn(
            animationSpec = tween(durationMillis = animDuration)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { if (showContent2.value) -it else it },
            animationSpec = tween(durationMillis = animDuration)
        ) + fadeOut(
            animationSpec = tween(durationMillis = animDuration)
        )
    ) {
        content2()
    }
}