package com.scatl.uestcbbs.compose.manager

import com.scatl.uestcbbs.compose.datastore.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by sca_tl at 2024/6/26 18:56:14
 */
object ThemeManager {

    const val DEFAULT_SEED_COLOR = "#FF0D253F"

    enum class DayNightMode(val value: Int) {
        FOLLOW_SYSTEM(0),
        DAY(1),
        NIGHT(2)
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
}