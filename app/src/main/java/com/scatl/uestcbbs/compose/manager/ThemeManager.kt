package com.scatl.uestcbbs.compose.manager

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.scatl.material_color_utilities.dynamiccolor.MaterialDynamicColors
import com.scatl.material_color_utilities.hct.Hct
import com.scatl.material_color_utilities.scheme.SchemeContent
import com.scatl.material_color_utilities.scheme.SchemeExpressive
import com.scatl.material_color_utilities.scheme.SchemeFidelity
import com.scatl.material_color_utilities.scheme.SchemeFruitSalad
import com.scatl.material_color_utilities.scheme.SchemeMonochrome
import com.scatl.material_color_utilities.scheme.SchemeNeutral
import com.scatl.material_color_utilities.scheme.SchemeRainbow
import com.scatl.material_color_utilities.scheme.SchemeTonalSpot
import com.scatl.material_color_utilities.scheme.SchemeVibrant
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.toIntColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by sca_tl at 2024/6/26 18:56:14
 */
object ThemeManager {

    enum class DayNightMode(val value: Int) {
        FOLLOW_SYSTEM(0),
        DAY(1),
        NIGHT(2)
    }

    enum class ThemeScheme(val value: String) {
        CONTENT("Content"),
        EXPRESSIVE("Expressive"),
        FIDELITY("Fidelity"),
        FRUIT_SALAD("FruitSalad"),
        MONOCHROME("Monochrome"),
        NEUTRAL("Neutral"),
        RAINBOW("Rainbow"),
        TONAL_SPOT("TonalSpot"),
        VIBRANT("Vibrant")
    }

    private val _useDynamicColor = MutableStateFlow(DataStore.dynamicTheme)
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor

    private val _dayNightMode = MutableStateFlow(DataStore.dayNightMode)
    val dayNightMode: StateFlow<Int> = _dayNightMode

    private val _customTheme = MutableStateFlow(DataStore.customTheme)
    val customTheme: StateFlow<String> = _customTheme

    private val _customThemeScheme = MutableStateFlow(DataStore.customThemeScheme)
    val customThemeScheme: StateFlow<String> = _customThemeScheme

    private val _appDarkMode = MutableStateFlow(false)
    val appDarkMode: StateFlow<Boolean> = _appDarkMode

    fun defaultLightScheme() = createCustomScheme(ThemeScheme.VIBRANT.value, seedColor = "#FF0D253F".toIntColor(), dark = false)
    fun defaultDarkScheme() = createCustomScheme(ThemeScheme.VIBRANT.value, seedColor = "#FF0D253F".toIntColor(), dark = true)

    var isAppDarkMode = false
        private set

    fun toggleUseDynamicColor(enable: Boolean) {
        _useDynamicColor.value = enable
        DataStore.dynamicTheme = enable
    }

    fun toggleDayNightMode(mode: DayNightMode) {
        _dayNightMode.value = mode.value
        DataStore.dayNightMode = mode.value
    }

    fun toggleCustomTheme(color: String) {
        _customTheme.value = color
        DataStore.customTheme = color
    }

    fun toggleCustomThemeScheme(scheme: String) {
        _customThemeScheme.value = scheme
        DataStore.customThemeScheme = scheme
    }

    fun toggleAppNightMode(night: Boolean) {
        _appDarkMode.value = night
        isAppDarkMode = night
    }

    fun createCustomScheme(themeScheme: String = DataStore.customThemeScheme, seedColor: Int, dark: Boolean): ColorScheme {
        val scheme = when(themeScheme) {
            ThemeScheme.CONTENT.value -> SchemeContent(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.EXPRESSIVE.value -> SchemeExpressive(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.FIDELITY.value -> SchemeFidelity(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.FRUIT_SALAD.value -> SchemeFruitSalad(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.MONOCHROME.value -> SchemeMonochrome(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.NEUTRAL.value -> SchemeNeutral(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.RAINBOW.value -> SchemeRainbow(Hct.fromInt(seedColor), dark, 0.0)
            ThemeScheme.TONAL_SPOT.value -> SchemeTonalSpot(Hct.fromInt(seedColor), dark, 0.0)
            else -> SchemeVibrant(Hct.fromInt(seedColor), dark, 0.0)
        }
        val materialDynamicColors = MaterialDynamicColors()

        return ColorScheme(
            primary = Color(materialDynamicColors.primary().getArgb(scheme)),
            onPrimary = Color(materialDynamicColors.onPrimary().getArgb(scheme)),
            primaryContainer = Color(materialDynamicColors.primaryContainer().getArgb(scheme)),
            onPrimaryContainer = Color(materialDynamicColors.onPrimaryContainer().getArgb(scheme)),
            secondary = Color(materialDynamicColors.secondary().getArgb(scheme)),
            onSecondary = Color(materialDynamicColors.onSecondary().getArgb(scheme)),
            secondaryContainer = Color(materialDynamicColors.secondaryContainer().getArgb(scheme)),
            onSecondaryContainer = Color(materialDynamicColors.onSecondaryContainer().getArgb(scheme)),
            tertiary = Color(materialDynamicColors.tertiary().getArgb(scheme)),
            onTertiary = Color(materialDynamicColors.onTertiary().getArgb(scheme)),
            tertiaryContainer = Color(materialDynamicColors.tertiaryContainer().getArgb(scheme)),
            onTertiaryContainer = Color(materialDynamicColors.onTertiaryContainer().getArgb(scheme)),
            error = Color(materialDynamicColors.error().getArgb(scheme)),
            onError = Color(materialDynamicColors.onError().getArgb(scheme)),
            errorContainer = Color(materialDynamicColors.errorContainer().getArgb(scheme)),
            onErrorContainer = Color(materialDynamicColors.onErrorContainer().getArgb(scheme)),
            background = Color(materialDynamicColors.background().getArgb(scheme)),
            onBackground = Color(materialDynamicColors.onBackground().getArgb(scheme)),
            surface = Color(materialDynamicColors.surface().getArgb(scheme)),
            onSurface = Color(materialDynamicColors.onSurface().getArgb(scheme)),
            surfaceTint = Color(materialDynamicColors.surfaceTint().getArgb(scheme)),
            surfaceVariant = Color(materialDynamicColors.surfaceVariant().getArgb(scheme)),
            onSurfaceVariant = Color(materialDynamicColors.onSurfaceVariant().getArgb(scheme)),
            outline = Color(materialDynamicColors.outline().getArgb(scheme)),
            outlineVariant = Color(materialDynamicColors.outlineVariant().getArgb(scheme)),
            scrim = Color(materialDynamicColors.scrim().getArgb(scheme)),
            inverseSurface = Color(materialDynamicColors.inverseSurface().getArgb(scheme)),
            inverseOnSurface = Color(materialDynamicColors.inverseOnSurface().getArgb(scheme)),
            inversePrimary = Color(materialDynamicColors.inversePrimary().getArgb(scheme)),
            surfaceDim = Color(materialDynamicColors.surfaceDim().getArgb(scheme)),
            surfaceBright = Color(materialDynamicColors.surfaceBright().getArgb(scheme)),
            surfaceContainerLowest = Color(materialDynamicColors.surfaceContainerLowest().getArgb(scheme)),
            surfaceContainerLow = Color(materialDynamicColors.surfaceContainerLow().getArgb(scheme)),
            surfaceContainer = Color(materialDynamicColors.surfaceContainer().getArgb(scheme)),
            surfaceContainerHigh = Color(materialDynamicColors.surfaceContainerHigh().getArgb(scheme)),
            surfaceContainerHighest = Color(materialDynamicColors.surfaceContainerHighest().getArgb(scheme)),
        )
    }
}