package com.scatl.uestcbbs.compose.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.elvishew.xlog.XLog
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.scatl.uestcbbs.compose.ext.hexToColor
import com.scatl.uestcbbs.compose.ext.isGTESdk31
import com.scatl.uestcbbs.compose.manager.ThemeManager

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val tag = "AppTheme"
    val useDynamicColor by ThemeManager.useDynamicColor.collectAsState()
    val customTheme by ThemeManager.customTheme.collectAsState()
    val customThemeScheme by ThemeManager.customThemeScheme.collectAsState()
    val dayNightMode by ThemeManager.dayNightMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val context = LocalContext.current

    val useDarkMode = rememberUpdatedState(
        newValue = (systemDark && dayNightMode == ThemeManager.DayNightMode.FOLLOW_SYSTEM.value) ||
                dayNightMode == ThemeManager.DayNightMode.NIGHT.value
    )
    ThemeManager.toggleAppNightMode(useDarkMode.value)

    @Composable
    fun getColorScheme(): ColorScheme {
        return when {
            useDynamicColor && customTheme.isEmpty() && isGTESdk31() -> {
                if (useDarkMode.value) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            }
            else -> {
                rememberDynamicColorScheme(
                    seedColor = if (customTheme.isEmpty()) ThemeManager.DEFAULT_SEED_COLOR.hexToColor() else customTheme.hexToColor(),
                    isDark = useDarkMode.value
                )
            }
        }
    }

    var colorScheme = getColorScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkMode.value
        }
    }

    key (useDynamicColor, customTheme, customThemeScheme, dayNightMode) {
        colorScheme = getColorScheme()
        XLog.tag(tag).d("theme change:${useDynamicColor}, ${customTheme}, ${customThemeScheme}, $dayNightMode")
    }

    CompositionLocalProvider(
        LocalCustomColors provides if (useDarkMode.value) DarkCustomColors else LightCustomColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )

//        DynamicMaterialTheme(
//            seedColor = seedColor,
//            isDark = useDarkMode.value,
//            animate = true,
//            content = content,
//        )
    }
}

@Composable
fun DarkTheme(
    content: @Composable () -> Unit
) {
    val useDynamicColor by ThemeManager.useDynamicColor.collectAsState()
    val customTheme by ThemeManager.customTheme.collectAsState()
    val customThemeScheme by ThemeManager.customThemeScheme.collectAsState()
    val context = LocalContext.current

    @Composable
    fun getColorScheme(): ColorScheme {
        return when {
            useDynamicColor && customTheme.isEmpty() && isGTESdk31() -> {
                dynamicDarkColorScheme(context)
            }
            else -> {
                rememberDynamicColorScheme(
                    seedColor = if (customTheme.isEmpty()) ThemeManager.DEFAULT_SEED_COLOR.hexToColor() else customTheme.hexToColor(),
                    style = PaletteStyle.entries.find { it.name == customThemeScheme } ?: PaletteStyle.Fidelity,
                    isDark = true,
                )
            }
        }
    }

    var colorScheme = getColorScheme()

    key (useDynamicColor, customTheme, customThemeScheme) {
        colorScheme = getColorScheme()
    }

    CompositionLocalProvider(
        LocalCustomColors provides DarkCustomColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}