package com.scatl.uestcbbs.compose.ext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * Created by sca_tl at 2024/8/19 13:54:51
 */
@Composable
fun Modifier.unboundClickable(
    radius: Dp = Dp.Unspecified,
    onClick: () -> Unit,
): Modifier {
    return this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = false, radius = radius),
        onClick = onClick
    )
}

@Composable
fun Modifier.randomBg(): Modifier {
    return this.background(
        color = Color.Red.copy(
            alpha = Random
                .nextDouble(0.0, 1.0)
                .toFloat()
        )
    )
}

fun Modifier.clickable(
    unbound: Boolean = true,
    radius: Dp = Dp.Unspecified,
    hapticFeedBack: Boolean = false,
    enable: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    if (enable) {
        val haptic = LocalHapticFeedback.current
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = !unbound, radius = radius),
        ) {
            if (hapticFeedBack) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            onClick()
        }
    } else {
        this
    }
}

@Composable
fun Modifier.commonCardBg(
    bgColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    containerColor: Color = MaterialTheme.colorScheme.background,
    padding: PaddingValues = PaddingValues(pagePadding + 5.dp),
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
): Modifier {
    return this.fillMaxWidth()
        .background(color = bgColor)
        .padding(horizontal = pagePadding, vertical = 5.dp)
        .background(
            color = containerColor,
            shape = RoundedCornerShape(cardCorner)
        )
        .clip(shape = RoundedCornerShape(cardCorner))
        .combinedClickable(
            onLongClick = {
                onLongClick?.invoke()
            },
            onClick = {
                onClick?.invoke()
            }
        )
        .padding(padding)
}