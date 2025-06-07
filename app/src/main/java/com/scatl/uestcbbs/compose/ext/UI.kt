package com.scatl.uestcbbs.compose.ext

import android.content.Context
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Created by sca_tl at 2024/4/26 13:51:42
 */
inline val Dp.dp2px: Float
    @Composable get() = with(LocalDensity.current) { this@dp2px.toPx() }

inline val Int.px2dp: Dp
    @Composable get() = with(LocalDensity.current) { this@px2dp.toDp() }

inline val Float.px2dp: Dp
    @Composable get() = with(LocalDensity.current) { this@px2dp.toDp() }

inline val Dp.dp2Sp: TextUnit
    @Composable get() = with(LocalDensity.current) { this@dp2Sp.toSp() }

inline val Int.dp2Sp: TextUnit
    @Composable get() = with(LocalDensity.current) { this@dp2Sp.dp.toSp() }

inline val Int.px2Sp: TextUnit
    @Composable get() = with(LocalDensity.current) { this@px2Sp.px2dp.toSp() }

inline val Int.sp2px: Float
    @Composable get() = with(LocalDensity.current) { this@sp2px.sp.toPx() }

@Composable
fun lerpColor(progress: Float, startColor: Color, endColor: Color): Color {
    return Color(
        alpha = lerp(startColor.alpha, endColor.alpha, progress),
        red = lerp(startColor.red, endColor.red, progress),
        green = lerp(startColor.green, endColor.green, progress),
        blue = lerp(startColor.blue, endColor.blue, progress)
    )
}

fun Color.toHexWithAlpha(): String {
    val argb = this.toArgb()
    val alpha = (argb shr 24) and 0xFF
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF
    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}

fun Color.toHexNoAlpha(): String {
    val argb = this.toArgb()
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF
    return String.format("#%02X%02X%02X", red, green, blue)
}

@Composable
fun measureTextWidth(text: String, style: TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer
        .measure(
            text = text,
            style = style
        )
        .size
        .width
    return widthInPixels.px2dp
}

@Composable
fun getStatusBarHeight(context: Context): Dp {
    val insets = ViewCompat.getRootWindowInsets((context as ComponentActivity).window.decorView)
    val statusBarHeightPx = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    return statusBarHeightPx.px2dp
}

fun getScreenRefreshRate(context: Context): Float {
    return if (isGTESdk30()) {
        context.display.refreshRate
    } else {
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay?.refreshRate ?: 60F
    }
}

val cardCorner = 6.dp
val pagePadding = 10.dp
val imageCorner = 5.dp